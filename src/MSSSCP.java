
public class MSSSCP {

    private static int POPULATION_SIZE = 80;
    private static int GENERATION_SIZE = 1000;
    private static double MUTATION_RATE = 0.89;

    // if waste is not in the fitness then we can reach 1730 cost
    public static void main(String[] args) {
        SpecificationDAO problem_spec = new SpecificationDAO("src/cutting_problem_instance.txt");
        System.out.println("\n\n>> Problem specs received ..");

        // Holds individual solutions, best individual score, average population fitness
        Population populous = new Population(POPULATION_SIZE, problem_spec.getBeams_in_stock(), problem_spec.getOrders());
        System.out.println("\n>> Population of solutions formulated ..");


        System.out.println("\n>> Mutation only GA ");
        GeneticAlgorithm mutation_only = new GeneticAlgorithm(false, MUTATION_RATE, GENERATION_SIZE, POPULATION_SIZE, populous, problem_spec.getBeams_in_stock(), problem_spec.getOrders());

        System.out.println("\n>> Mutation & Recombination GA ");
        GeneticAlgorithm mutation_recombination = new GeneticAlgorithm(true, MUTATION_RATE, GENERATION_SIZE, POPULATION_SIZE, populous, problem_spec.getBeams_in_stock(), problem_spec.getOrders());
    }
}
