import java.util.List;

/**
 * The main entry point for the Airport Simulation program.
 * This class initializes the airport data processor, loads the necessary data,
 * and starts the simulation engine.
 */
public class Main {

    /**
     * The main method that initializes and starts the airport simulation.
     *
     * @param args Command-line arguments (not used in this program).
     */
    public static void main(String[] args) {
        System.out.println("Initializing Airport Data Processor...\n");

        // Initialize the processor and load all data
        AirportDataProcessor dataProcessor = new AirportDataProcessor();

        // Retrieve loaded data
        List<Airport> airports = dataProcessor.getAirports();
        List<Airplane> airplanes = dataProcessor.getAirplanes();
        List<Passengers> passengers = dataProcessor.getPassengers();

        // Check if all necessary data is loaded before starting the simulation
        if (airports.isEmpty() || airplanes.isEmpty() || passengers.isEmpty()) {
            System.out.println("[ERROR]: Missing essential data. Simulation cannot start.");
            return;
        }

        // Display loaded data
        dataProcessor.displayLoadedData();

        // Run a simple simulation with 3 cycles
        SimulationEngine simulation = new SimulationEngine(airports, airplanes, passengers);
        simulation.runSimulation();
    }
}




