import java.util.*;

public class Population {
    private PlantBeams bestSolution = null;
    private double avgFitnessCost = 0.0;
    private ArrayList<PlantBeams> population = new ArrayList<PlantBeams>();

    // A candidate solution satisfies all orders, remembers how many beams used and to which beam an order was assigned.
    // beam lengths can be generated Ad infinitum.
    // As beam lengths are used, the cost metric increases and the number of orders decreases.
    public Population(int populationSize, HashMap<Integer, Float> beams, ArrayList<Order> items) {
        for (int i = 0; i < populationSize; i++)
            population.add(generateCandidateSolution(beams, items));
        findBest();  // Pattern with best Score in population
        calculateAvgFitness();  // Average fitness score of population
    }

    public Population(ArrayList<PlantBeams> chosenPop){
        population = chosenPop;
        findBest();
        calculateAvgFitness();
    }

    public PlantBeams generateCandidateSolution(HashMap<Integer, Float> rods, ArrayList<Order> orders) {
        List<Order> items = new ArrayList<Order>(orders.size());
        for (Order x : orders)
            items.add(new Order(x.getLength(), x.getQuantity()));

        //Candidate solution remembers items satisfied, beams used and the overall cost of operation.
        PlantBeams plant = new PlantBeams();

        //Generation process
        while (items.size() > 0) {
            // Get random order and delete it from list of unanswered orders.
            int pos = new Random(46).nextInt(items.size());

            Order order = items.get(pos);
            items.remove(pos);

            // Current item specs to satisfy
            int length_to_satisfy = order.getLength();
            int quantity_to_satisfy = order.getQuantity();

            // By quantity, for each item, allocate length to an operational beam if possible or create new beam to use.
            for (int i = 0; i < quantity_to_satisfy; i++) {
                // Check first if there is a beam in use that could satisfy the ordered item
                if (!plant.hasLength(length_to_satisfy)) {
                    // Get a new random in-stock beam length superior to that of the item to satisfy length.
                    Object[] values = rods.keySet().toArray();
                    int random_beam_length;
                    do {
                        random_beam_length = (int) values[new Random().nextInt(values.length)];
                    } while (length_to_satisfy > random_beam_length);
                    // Add beam to lengths in use with their cost and satisfy ordered item
                    plant.reinforce(random_beam_length, rods.get(random_beam_length));
                }
                // fill the length in use
                plant.satisfy(length_to_satisfy);
            }
        }
        return plant;
    }

    public PlantBeams findBest() {
        bestSolution = getIndividual(0);
        for (PlantBeams individual : population)
            bestSolution = (bestSolution.getSum_cost() >= individual.getSum_cost()
                    && bestSolution.getWaste()>=individual.getWaste()) ? individual : bestSolution;
        return bestSolution;
    }

    private void calculateAvgFitness() {
        for (PlantBeams solution : population)
            avgFitnessCost += solution.getSum_cost();
        avgFitnessCost /= population.size();
    }

    public double getAvgFitnessCost() { return avgFitnessCost; }

    public void addIndividual(PlantBeams individual) {
        if (individual != null) population.add(individual);
    }

    public PlantBeams getIndividual(int id) { return population.get(id); }

    public PlantBeams getBest() {
        return bestSolution;
    }

    public ArrayList<PlantBeams> getPopulation() {
        return population;
    }

    public int getPopulationSize() {
        return population.size();
    }
}
