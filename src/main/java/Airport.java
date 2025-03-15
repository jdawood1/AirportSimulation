/**
 * Airport Resources:
 * – Limited number of gates, runways, and security checkpoints.
 * – Manage the allocation of resources to avoid conflicts and delays.
 * – Implement strategies for handling peak hours with increased traffic.
 */

public interface Airport {

    /**
     * General Information.
     *
     * @return The name of the airport.
     */
    String getName();

    /**
     * Capacity & Availability.
     *
     * @return The total number of gates at the airport.
     */
    int getTotalGates();

    /**
     * Returns the total number of runways at the airport.
     *
     * @return The total number of runways.
     */
    int getTotalRunways();

    /**
     * Gate Management.
     *
     * @return The next available gate number.
     */
    int getNextAvailableGate();

    /**
     * Releases a gate, making it available for new assignments.
     *
     * @param gateNumber The gate number to release.
     */
    void releaseGate(int gateNumber);

    /**
     * Updates the status of gate usage.
     */
    void updateGateUsage();

    /**
     * Runway Management.
     *
     * @param airplane The airplane requesting a runway.
     * @return True if a runway is available, false otherwise.
     */
    boolean requestRunway(AirplaneModel airplane);

    /**
     * Releases a previously occupied runway.
     *
     * @param airplane The airplane that occupied the runway.
     */
    void releaseRunway(AirplaneModel airplane);

    /**
     * Returns the number of currently available runways.
     *
     * @return The number of available runways.
     */
    int getAvailableRunways();

    /**
     * Marks a runway as occupied.
     */
    void markRunwayOccupied();

    /**
     * Updates the status of runway usage.
     */
    void updateRunwayUsage();

    /**
     * Security & Passenger Processing.
     *
     * @param passenger The passenger undergoing security processing.
     */
    void processPassengerSecurity(Passengers passenger);

    /**
     * Checks if the airport's security is overloaded.
     *
     * @return True if security is overloaded, false otherwise.
     */
    boolean isSecurityOverloaded();

    /**
     * Peak Hour Traffic Handling.
     * Manages traffic and resource allocation during peak hours.
     */
    void handlePeakHours();
}

