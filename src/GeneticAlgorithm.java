import java.util.*;

public class GeneticAlgorithm {
    private PlantBeams apexSolution;
    private Population original_population;
    private List<Order> items_to_satisfy;
    private HashMap<Integer, Float> rods;

    public GeneticAlgorithm(boolean type, double MUTATION_RATE, int generations, int populationSize, Population population, HashMap<Integer, Float> beams_supply, ArrayList<Order> orders) {
        rods = beams_supply;
        original_population = population;
        items_to_satisfy = orders;

        int PARENTS_POPULATION_SIZE = populationSize / 2;
        int OFFSPRING_POPULATION_SIZE = populationSize * 2;

        ArrayList<PlantBeams> Bestsurvivors = new ArrayList<>();
        for (int i = 0; i < generations; i++) {
            //System.out.println(">> Parent Solutions Selection");
            ArrayList<PlantBeams> parents = parents_selection(PARENTS_POPULATION_SIZE);
            ArrayList<PlantBeams> children = null;
            ArrayList<PlantBeams> mutatedChildren = null;
            ArrayList<PlantBeams> survivors = new ArrayList<>();
            if (type) {
                children = single_point_crossover(parents, OFFSPRING_POPULATION_SIZE);
                mutatedChildren = partial_shuffle_mutator(children, MUTATION_RATE);
                survivors.add(best_in_generation(parents));
            } else
                mutatedChildren = partial_shuffle_mutator(parents, MUTATION_RATE);

            survivors.addAll(filter_offspring(mutatedChildren, populationSize));

            PlantBeams best_ = best_in_generation(survivors);
            Bestsurvivors.add(best_);
            System.out.println("\t"+i +", "+ best_.getSum_cost() + ", " + best_.getWaste());

            original_population = new Population(survivors);
        }
        apexSolution = best_in_generation(Bestsurvivors);
        //System.out.println("Best solution cost: " + apexSolution.getSum_cost() + " waste:" + apexSolution.getWaste());
    }

    private ArrayList<PlantBeams> partial_shuffle_mutator(ArrayList<PlantBeams> children, double mutation_rate) {
        ArrayList<PlantBeams> mutatedChildren = new ArrayList<>();
        for (PlantBeams child : children) {
            double nature = new Random().nextInt(101) / 100.0;
            if (nature <= mutation_rate) {
                // Get random beam from child solution
                List<Beam> genes = child.getBeams_in_use();
                Beam x = genes.get(new Random().nextInt(genes.size()));
                // get sum of item lengths satisfied by current beam;
                ArrayList<Integer> to_change = x.getItem_lengths_satisfied();
                // mutate orders not necessarily to its benefit but  ===> it must be valid
                boolean mutatation = false;
                while (!mutatation) {
                    Beam target = genes.get(new Random().nextInt(genes.size()));
                    ArrayList<Integer> items_in_target = target.getItem_lengths_satisfied();

                    int itemLengths = 0;
                    for (Integer integer : to_change)
                        itemLengths += integer;

                    int targetLengths = 0;
                    for (Integer integer : items_in_target)
                        targetLengths += integer;

                    int l1 = itemLengths + x.getBeam_length_left();
                    int l2 = targetLengths + target.getBeam_length_left();

                    if (l2 >= itemLengths && l1 >= targetLengths) {
                        x.setItem_lengths_satisfied(items_in_target);
                        target.setItem_lengths_satisfied(to_change);
                        mutatation = true;
                    }
                }
                if (x.getBeam_length_left() != 0) {
                    int satisfy = 0;
                    for (int sum : x.getItem_lengths_satisfied())
                        satisfy += sum;
                    //Randomly pick another beam length that satisfies the item lengths.
                    int random_beam_length;
                    Object[] values = rods.keySet().toArray();
                    do {
                        random_beam_length = (int) values[new Random().nextInt(values.length)];
                    } while (satisfy > random_beam_length);
                    // Update Beam object
                    x.setBeam_length_left(random_beam_length - satisfy);
                    x.setOriginal_beam_cost(rods.get(random_beam_length));
                }
            }
            mutatedChildren.add(child);
        }
        return mutatedChildren;
    }

    private ArrayList<PlantBeams> filter_offspring(ArrayList<PlantBeams> mutatedChildren, int size) {
        ArrayList<PlantBeams> population = new ArrayList<>();
        for (int i = 0; i < size; i++)
            population.add(best_in_generation(mutatedChildren));
        return population;
    }

    /**
     * Select the best (lowest in cost & waste: fitness) from population_size/2 parents to generate offspring.
     */
    private ArrayList<PlantBeams> parents_selection(int parents_population_size) {
        ArrayList<PlantBeams> first_generation = new ArrayList<>();
        for (int i = 0; i < parents_population_size; i++) {
            ArrayList<PlantBeams> selected = new ArrayList<>();
            for (int j = 0; j < parents_population_size; j++) {
                int index = new Random(46).nextInt(original_population.getPopulationSize());
                PlantBeams candidateParent = new PlantBeams();
                candidateParent.setBeams_in_use(original_population.getIndividual(index).getBeams_in_use());
                selected.add(candidateParent);
            }
            first_generation.add(best_in_generation(selected));
        }
        return first_generation;
    }

    /**
     * Marry random parents and generate offspring solutions.
     */
    private ArrayList<PlantBeams> single_point_crossover(ArrayList<PlantBeams> parents, int offspring_population_size) {
        ArrayList<PlantBeams> offspring = new ArrayList<>();
        for (int i = 0; i < offspring_population_size; i++) {
            PlantBeams parent1 = parents.get(new Random().nextInt(parents.size()));
            PlantBeams parent2 = parents.get(new Random().nextInt(parents.size()));
            offspring.add(proCreate(parent1, parent2));
        }
        return offspring;
    }

    /**
     * ::Helper function:: generate one valid offspring solution.
     */
    private PlantBeams proCreate(PlantBeams parent1, PlantBeams parent2) {
        List<Beam> p1_beams = parent1.getBeams_in_use();
        List<Beam> p2_beams = parent2.getBeams_in_use();
        List<Beam> offspring_beams = new ArrayList<>();
        int size = p1_beams.size();

        // randomly pick a chromosome from parent 1 solution.
        int randomlyPicked = new Random().nextInt(size);
        for (int i = 0; i < size * 2 / 3; i++) {
            Beam one = p1_beams.get((i + randomlyPicked) % size);
            offspring_beams.add(one);
        }

        // record all lengths satisfied by parent_1_chromosome
        ArrayList<Integer> items_satisfied_p1_chromosome = new ArrayList<>();
        for (Beam l1 : offspring_beams)
            items_satisfied_p1_chromosome.addAll(l1.getItem_lengths_satisfied());

        // copy p2_beams and remove satisfied orders by parent_1
        List<Beam> p2_ = new ArrayList<>(p2_beams.size());
        for (Beam x : p2_beams) {
            Beam n = new Beam(x.getBeam_length_left(), x.getOriginal_beam_cost());
            for (Integer item : x.getItem_lengths_satisfied())
                n.getItem_lengths_satisfied().add(item);
            p2_.add(n);
        }

        for (int length_satisfied : items_satisfied_p1_chromosome)
            for (Beam x : p2_)
                if (x.getItem_lengths_satisfied().contains(length_satisfied)) {
                    x.getItem_lengths_satisfied().remove(Integer.valueOf(length_satisfied));
                    break;
                }

        p2_.removeIf(x -> x.getItem_lengths_satisfied().size() == 0);
        offspring_beams.addAll(p2_);
        // tell me which orders did you satisfy
        for (Beam l2 : offspring_beams)
            for (Order l1 : items_to_satisfy)
                for (Integer item : l2.getItem_lengths_satisfied())
                    if (l1.getLength() == item)
                        l1.setQuantity(l1.getQuantity() - 1);
        items_to_satisfy.removeIf(x -> x.getQuantity() == 0);

        // what is left is to satisfy the discrepancy of the ordered items.

        PlantBeams child = new PlantBeams(offspring_beams);
        while (items_to_satisfy.size() > 0) {
            int pos = new Random(46).nextInt(items_to_satisfy.size());
            Order order = items_to_satisfy.get(pos);
            items_to_satisfy.remove(pos);
            // Current item specs to satisfy
            int length_to_satisfy = order.getLength();
            int quantity_to_satisfy = order.getQuantity();

            for (int i = 0; i < quantity_to_satisfy; i++) {
                // Check first if there is a beam in use that could satisfy the ordered item
                if (!child.hasLength(length_to_satisfy)) {
                    // Get a new random in-stock beam length superior to that of the item to satisfy length.
                    Object[] values = rods.keySet().toArray();
                    int random_beam_length;
                    do {
                        random_beam_length = (int) values[new Random().nextInt(values.length)];
                    } while (length_to_satisfy > random_beam_length);

                    child.reinforce(random_beam_length, rods.get(random_beam_length));
                }
                child.satisfy(length_to_satisfy);
            }
        }
        // System.out.println("child cost, "+child.getSum_cost()+", child waste, "+child.getWaste());
        return child; // valid child solution
    }

    //Fitness is considered along two axes Cost and waste.
    private PlantBeams best_in_generation(ArrayList<PlantBeams> offspring) {
        PlantBeams apexPredator = null;
        if (offspring != null) {
            apexPredator = offspring.get(0);
            for (PlantBeams individual : offspring)
                apexPredator = (apexPredator.getSum_cost() >= individual.getSum_cost() ) ? individual : apexPredator;
        }
        return apexPredator;
    }

    public PlantBeams getApexSolution() {
        return apexSolution;
    }
}
