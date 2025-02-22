/**
 * Simulation (must have):
 * – Run the simulation in cycles (e.g., each cycle represents 30 minutes).
 * – Display the status of the airport (arrivals, departures, gate assignments) at each cycle.
 * – Generatereportsorlogsofairportactivity(e.g., average waiting times, resource utilization).
 */

public interface Simulation {

    /**
     * Starts and runs the simulation in cycles, where each cycle represents a fixed time duration.
     */
    void runSimulation();

    /**
     * Displays the status of airport operations for each simulation cycle.
     */
    void displayAirportStatus();

    /**
     * Retrieves the current simulation time.
     * This method is useful for tracking time progression within the simulation.
     *
     * @return The current simulation time as a formatted string.
     */
    String getCurrentSimulationTime();
}