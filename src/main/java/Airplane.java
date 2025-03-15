import java.time.LocalDateTime;

/**
 * Airplanes:
 * – Different types of airplanes with varying capacities and fuel efficiencies.
 * – Airplanes arrive at the airport and need to be assigned gates.
 * – Airplanes depart for pre-determined destinations after a set amount of time.
 * – Simulate refueling and maintenance procedures.
 */

public interface Airplane {

    /**
     * Airplane Information.
     */
    String getType();

    /**
     * Returns the seating or cargo capacity of the airplane.
     */
    int getCapacity();

    /**
     * Returns the fuel efficiency of the airplane in gallons per mile.
     */
    double getFuelEfficiency();

    /**
     * Assigned Airport & Gate Management.
     */
    String getAssignedAirport();

    /**
     * Returns the gate assigned to the airplane.
     */
    int getAssignedGate();

    /**
     * Assigns a gate number and airport to the airplane.
     *
     * @param gateNumber The gate number.
     * @param airport The airport where the airplane is assigned.
     */
    void assignGate(int gateNumber, Airport airport);

    /**
     * Destination & Scheduling.
     */
    String getDestination();

    /**
     * Returns the departure time of the airplane.
     */
    LocalDateTime getDepartureTime();

    /**
     * Returns the flight duration in minutes.
     */
    int getFlightDuration();

    /**
     * Flight Operations.
     */
    void depart();

    /**
     * Delays the arrival of the airplane.
     */
    void delayArrival();

    /**
     * Delays the departure based on weather or congestion conditions.
     *
     * @param weatherDelay Indicates if the delay is due to weather.
     * @param congestionDelay Indicates if the delay is due to airport congestion.
     */
    void delayDeparture(boolean weatherDelay, boolean congestionDelay);

    /**
     * Maintenance & Fuel Management.
     */
    void refuel();

    /**
     * Performs routine maintenance on the airplane.
     */
    void performMaintenance();

    /**
     * Checks if the airplane has sufficient fuel for the journey.
     *
     * @return true if the airplane has enough fuel, false otherwise.
     */
    boolean hasSufficientFuel();
}

