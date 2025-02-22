import java.time.LocalDateTime;

/**
 * Passengers:
 * – Passengers arrive at the airport for different flights.
 * – Passengers check-in, go through security, and board their assigned flights.
 * – Simulate delays or issues with passenger processing.
 */

public interface Passengers {

    /**
     * Retrieves the passenger's name.
     *
     * @return The name of the passenger.
     */
    String getName();

    /**
     * Retrieves the passenger's arrival time at the airport.
     *
     * @return The arrival time of the passenger.
     */
    LocalDateTime getArrivalTime();

    /**
     * Checks if the passenger has arrived at the airport.
     *
     * @return True if the passenger has arrived, false otherwise.
     */
    boolean hasArrived();

    /**
     * Retrieves the flight assigned to the passenger.
     *
     * @return The assigned flight.
     */
    AirplaneModel getAssignedFlight();

    /**
     * Checks if the passenger has completed the check-in process.
     *
     * @return True if the passenger has checked in, false otherwise.
     */
    boolean hasCheckedIn();

    /**
     * Checks if the passenger has cleared security.
     *
     * @return True if the passenger has cleared security, false otherwise.
     */
    boolean hasClearedSecurity();

    /**
     * Checks if the passenger has boarded the flight.
     *
     * @return True if the passenger has boarded, false otherwise.
     */
    boolean hasBoarded();

    /**
     * Simulates the passenger's arrival at the airport.
     */
    void arriveAtAirport();

    /**
     * Simulates the check-in process for the passenger.
     */
    void checkIn();

    /**
     * Simulates the passenger clearing security.
     */
    void clearSecurity();

    /**
     * Simulates the passenger boarding the flight.
     */
    void boardFlight();

    /**
     * Retrieves the passenger's estimated wait time at security.
     *
     * @return The security wait time in minutes.
     */
    int getSecurityWaitTime();

    /**
     * Sets the estimated wait time for security processing.
     *
     * @param time The security wait time in minutes.
     */
    void setSecurityWaitTime(int time);

    /**
     * Checks if the passenger has received a delay due to peak-hour congestion.
     *
     * @return True if the passenger has been delayed, false otherwise.
     */
    boolean hasReceivedPeakHourDelay();

    /**
     * Sets whether the passenger has been delayed due to peak-hour congestion.
     *
     * @param status True if the passenger has received the delay, false otherwise.
     */
    void setHasReceivedPeakHourDelay(boolean status);
}