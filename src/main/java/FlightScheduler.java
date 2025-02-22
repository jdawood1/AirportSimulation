import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Manages flight scheduling, including departures, arrivals,
 * and passenger processing.
 */
public class FlightScheduler {
    private final List<Airplane> inFlightPlanes;
    private final List<Airplane> arrivedPlanes;
    private final Map<Airplane, LocalDateTime> departureSchedule;
    private final List<Passengers> passengers;
    private static final Random random = new Random();
    private final SimulationEngine simulationEngine;

    /**
     * Constructs a FlightScheduler with the given simulation engine and passenger list.
     *
     * @param simulationEngine The simulation engine managing the system.
     * @param passengers       The list of passengers for flights.
     */
    public FlightScheduler(SimulationEngine simulationEngine, List<Passengers> passengers) {
        this.simulationEngine = Objects.requireNonNull(
                simulationEngine, "SimulationEngine cannot be null");
        this.inFlightPlanes = new ArrayList<>();
        this.arrivedPlanes = new ArrayList<>();
        this.departureSchedule = new HashMap<>();
        this.passengers = new ArrayList<>(passengers);
    }

    /**
     * Adds a new scheduled flight.
     *
     * @param airplane      The airplane to schedule.
     * @param departureTime The departure time of the flight.
     */
    public void scheduleFlight(Airplane airplane, LocalDateTime departureTime) {
        departureSchedule.put(airplane, departureTime);
        inFlightPlanes.add(airplane);
    }

    /**
     * Processes arriving flights and handles maintenance and refueling.
     *
     * @param currentTime    The current simulation time.
     * @param airportManager The airport manager responsible for handling airport operations.
     */
    public void processArrivals(LocalDateTime currentTime, AirportManager airportManager) {
        System.out.println("\n✈ Arrival and Maintenance Processing:");
        Iterator<Airplane> iterator = inFlightPlanes.iterator();
        boolean anyArrivals = false;

        while (iterator.hasNext()) {
            AirplaneModel airplane = (AirplaneModel) iterator.next();

            // Randomized arrival window (30 to 60 minutes before departure)
            LocalDateTime earliestArrival = airplane.getDepartureTime().minusMinutes(60);
            LocalDateTime latestArrival = airplane.getDepartureTime().minusMinutes(30);

            if (currentTime.isAfter(earliestArrival) && currentTime.isBefore(latestArrival)) {
                anyArrivals = true;
                Airport destination = airportManager.getAirportByName(airplane.getDestination());

                if (destination != null) {
                    int gateNumber = destination.getNextAvailableGate();
                    if (gateNumber != -1) {
                        // Reduce Fuel Before Arrival
                        airplane.consumeFuel();

                        // Assign gate, mark as arrived
                        airplane.assignGate(gateNumber, destination);
                        arrivedPlanes.add(airplane);
                        iterator.remove();

                        System.out.println("🛬 " + airplane.getType() + " (" + airplane.getModel()
                                + ") has arrived at " + destination.getName() + " (Arrived at: "
                                + currentTime.format(DateTimeFormatter.ofPattern("HH:mm")) + ")."
                        );

                        // Check if refueling is needed**
                        if (airplane.needsRefueling()) {
                            airplane.refuel();
                            System.out.println("⛽ " + airplane.getType()
                                + " (" + airplane.getModel() + ") is refueling at "
                                + destination.getName()
                            );
                        } else {
                            System.out.println("⛽ " + airplane.getType()
                                + " (" + airplane.getModel() + ") has sufficient fuel."
                            );
                        }

                        // Maintenance Check
                        if (shouldPerformMaintenance(airplane)) {
                            airplane.performMaintenance();
                            System.out.println("🛠 " + airplane.getType()
                                + " (" + airplane.getModel()
                                + ") is undergoing maintenance at "
                                + destination.getName()
                            );
                        }

                        // Update Gate & Runway Usage
                        destination.updateGateUsage();
                        destination.updateRunwayUsage();
                    } else {
                        // No available gate, delay arrival
                        airplane.delayArrival();
                        System.out.println("⚠ " + airplane.getType()
                                + " (" + airplane.getModel()
                                + ") delayed due to no available gates at "
                                + destination.getName()
                        );
                    }
                }
            }
        }

        if (!anyArrivals) {
            System.out.println("\033[1;31mNo airplane arrivals or "
                    + "maintenance tasks were processed this cycle.\033[0m");
        }
    }

    /**
     * Determines whether an airplane requires maintenance.
     *
     * @param airplane The airplane to check.
     * @return True if maintenance is required, false otherwise.
     */
    private boolean shouldPerformMaintenance(AirplaneModel airplane) {
        int flightDuration = airplane.getFlightDuration();
        boolean needsMaintenance = false;

        if (flightDuration > 120) { // Long flights → 50% chance
            needsMaintenance = random.nextInt(100) < 50;
        } else if (flightDuration > 60) { // Medium flights → 30% chance
            needsMaintenance = random.nextInt(100) < 30;
        } else { // Short flights → 15% chance
            needsMaintenance = random.nextInt(100) < 15;
        }

        return needsMaintenance;
    }

    /**
     * Processes airplane departures and ensures all necessary checks are completed.
     *
     * @param currentTime    The current simulation time.
     * @param airportManager The airport manager responsible for airport operations.
     */
    public void processDepartures(LocalDateTime currentTime, AirportManager airportManager) {
        System.out.println("\n✈ Departures Processing:");
        Iterator<Airplane> iterator = arrivedPlanes.iterator();
        boolean anyDepartures = false;

        while (iterator.hasNext()) {
            AirplaneModel airplane = (AirplaneModel) iterator.next();
            LocalDateTime scheduledDeparture = departureSchedule.getOrDefault(airplane, null);

            if (scheduledDeparture == null || scheduledDeparture.isAfter(currentTime)) {
                continue;
            }

            Airport airport = airportManager.getAirportByName(airplane.getAssignedAirport());
            if (airport == null) {
                System.out.println("[Error]: Airport not found for "
                        + airplane.getAssignedAirport());
                continue;
            }

            // Skip passenger check for CARGO planes
            if (!airplane.getType().equalsIgnoreCase("CARGO")) {
                boolean allPassengersBoarded = getPassengersForFlight(airplane)
                        .stream().allMatch(Passengers::hasBoarded);

                if (!allPassengersBoarded) {
                    System.out.println("⚠ " + airplane.getType() + " at "
                            + airplane.getAssignedAirport()
                            + " delayed due to passengers still boarding.");
                    airplane.delayDeparture(false, false);
                    continue;
                }
            }

            // Fuel Check Before Departure
            if (!airplane.hasSufficientFuel()) {
                System.out.println("⛽ " + airplane.getType() + " at "
                        + airplane.getAssignedAirport() + " delayed for refueling.");
                airplane.refuel();
                airplane.delayDeparture(false, false);
                continue;
            }

            // Request runway for departure
            boolean runwayAvailable = airport.requestRunway(airplane);
            if (runwayAvailable) {
                System.out.println("🛫 " + airplane.getType()
                        + " is using a runway at " + airport.getName() + "...");

                airport.releaseGate(airplane.getAssignedGate());
                airport.markRunwayOccupied();

                airplane.depart();
                iterator.remove();
                //totalFlightsProcessed++;

                airport.releaseRunway(airplane);
                anyDepartures = true;

                System.out.println("✈ " + airplane.getType()
                        + " departed successfully from " + airport.getName());

                // Mark all passengers on this flight as 'In Flight'
                for (Passengers passenger : getPassengersForFlight(airplane)) {
                    if (passenger.hasBoarded()) {
                        simulationEngine.updatePassengerLogToInFlight(passenger, airplane);
                    }
                }

                // Update Runway & Gate Status
                airport.updateGateUsage();
                airport.updateRunwayUsage();
            } else {
                System.out.println("🚦 " + airplane.getType()
                        + " delayed due to congestion at " + airport.getName());
                airplane.delayDeparture(false, true);
            }
        }

        if (!anyDepartures) {
            System.out.println("\033[1;31mNo airplane "
                    + "departures were processed this cycle.\033[0m");
        }
    }

    /**
     * Retrieves all passengers assigned to a specific airplane.
     *
     * @param airplane The airplane for which passengers are retrieved.
     * @return A list of passengers assigned to the given airplane.
     */
    public List<Passengers> getPassengersForFlight(Airplane airplane) {
        return passengers.stream()
                .filter(passenger -> passenger.getAssignedFlight().equals(airplane))
                .collect(Collectors.toList());
    }

    /**
     * Retrieves a list of arrived planes.
     *
     * @return A copy of the list of arrived planes.
     */
    public List<Airplane> getArrivedPlanes() {
        return Collections.unmodifiableList(arrivedPlanes); // Ensures immutability
    }


}
