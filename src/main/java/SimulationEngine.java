import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

/**
 * The SimulationEngine manages and runs the airport simulation.
 * It processes passenger check-ins, security clearance, cargo flights,
 * and flight scheduling. The simulation runs in cycles, each representing a fixed time interval.
 */
public class SimulationEngine implements Simulation {
    private final List<Airport> airports;
    private final List<Passengers> passengers;
    private LocalDateTime currentTime;
    private static final Random random = new Random();
    private final FlightScheduler flightScheduler;
    private static final DateTimeFormatter timeFormatter =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private final List<String> boardedPassengersLog = new ArrayList<>();

    // Track airplanes that have arrived at their airports
    private final Set<Airplane> arrivedPlanes = new HashSet<>();
    private final Map<Airplane, Airport> airplaneAirportMap = new HashMap<>();

    /**
     * Constructs the SimulationEngine with airport, airplane, and passenger data.
     * It initializes flight scheduling and sets up simulation time.
     *
     * @param airports   List of airports in the simulation.
     * @param airplanes  List of airplanes in the simulation.
     * @param passengers List of passengers in the simulation.
     */
    public SimulationEngine(List<Airport> airports,
                            List<Airplane> airplanes,
                            List<Passengers> passengers) {
        this.airports = new ArrayList<>(airports);
        this.passengers = new ArrayList<>(passengers);
        this.flightScheduler = new FlightScheduler(this, passengers);
        this.currentTime = LocalDateTime.now();

        // Schedule initial flights
        for (Airplane airplane : airplanes) {
            flightScheduler.scheduleFlight(airplane, airplane.getDepartureTime());
            Airport destination = getAirportByName(airplane.getDestination());
            if (destination != null) {
                airplaneAirportMap.put(airplane, destination);
            }
        }
    }

    /**
     * Runs the airport simulation in cycles.
     * Each cycle processes passenger check-ins, security, flight arrivals, departures,
     * and airport activities.
     */
    @Override
    public void runSimulation() {
        System.out.println("\n\033[1;92mStarting airport simulation...\033[0m");

        int cycles = 1;
        while (!allPassengersBoarded() || !allCargoDeparted()) {
            System.out.println("\n=== Cycle " + cycles + " " + getCurrentSimulationTime() + " ===");
            flightScheduler.processArrivals(currentTime, new AirportManager(airports));
            processPassengerArrival();
            processCheckIn();
            processSecurity();
            processCargoFlights();
            processWaitingPassengers();
            removeMissedPassengers();
            processBoarding();
            processPeakHours();
            flightScheduler.processDepartures(currentTime, new AirportManager(airports));
            displayAirportStatus();
            currentTime = currentTime.plusMinutes(30);
            cycles++;
        }

        System.out.println("\n\033[1;92mSimulation completed. "
                + "All passengers have boarded, and all flights have departed.\033[0m");
    }

    private void processCargoFlights() {
        System.out.println("\n📦 Cargo Flight Processing:");

        boolean anyCargoUpdates = false;

        for (Airplane airplane : flightScheduler.getArrivedPlanes()) {
            if (!airplane.getType().equalsIgnoreCase("CARGO")) {
                continue;
            }

            AirplaneModel cargoPlane = (AirplaneModel) airplane;
            String cargoType = getRandomCargoType();
            int cargoLoad = getRandomCargoLoad();

            System.out.println("🚛 CARGO LOADED: "
                    + cargoPlane.getModel() + " is transporting "
                    + cargoLoad + " tons of " + cargoType
                    + " to " + cargoPlane.getDestination()
            );

            // Add custom delays based on a cargo type
            if (cargoType.equalsIgnoreCase("medical supplies")
                    || cargoType.equalsIgnoreCase("perishables")) {
                System.out.println("⚠ URGENT SHIPMENT! Extra security clearance required.");
                cargoPlane.delayDeparture(false, false);
            }

            // Mid-flight updates
            if (cargoPlane.isAirborne()) {
                System.out.println("📡 LIVE TRACKING: " + cargoPlane.getModel() + " en route to "
                        + cargoPlane.getDestination() + ". ETA: "
                        + cargoPlane.getEstimatedArrivalTime().format(
                                DateTimeFormatter.ofPattern("HH:mm"))
                );
            }

            anyCargoUpdates = true;
        }

        if (!anyCargoUpdates) {
            System.out.println("\033[1;31mNo cargo updates this cycle.\033[0m");
        }
    }

    private void removeMissedPassengers() {
        System.out.println("\nMissed Flights Processing:");
        Iterator<Passengers> iterator = passengers.iterator();
        boolean anyMissedFlights = false; // Track if any passenger misses their flight

        // 🎭 List of funny excuses
        List<String> funnyExcuses = Arrays.asList(
                "But I was only 2 hours late! Planes should wait for important people like me!",
                "I swear the gate was just here a second ago… Where did it go?",
                "My horoscope said today would be a smooth journey… It lied.",
                "I thought my ticket said PM, not AM… Wait, it’s the same thing, right?",
                "Can’t you just turn the plane around? I forgot my lucky socks at home!",
                "If the pilot is still in the airport, the plane is technically still here!",
                "I was busy getting the perfect airport selfie, priorities matter!",
                "I ran towards the gate… but my coffee told me to walk!",
                "I was in the bathroom and assumed the plane would wait for me!",
                "I saw the plane take off and thought, ‘That’s not my flight!’ Turns out, it was."
        );

        while (iterator.hasNext()) {
            Passengers passenger = iterator.next();
            Airplane assignedFlight = findAirplaneForPassenger(passenger);

            if (assignedFlight != null
                    && currentTime.isAfter(assignedFlight.getDepartureTime())
                    && !passenger.hasBoarded()) {

                // Use the shared `Random` object instead of creating a new one
                String funnyComment = funnyExcuses.get(random.nextInt(funnyExcuses.size()));

                System.out.println("\033[1;31m" + passenger.getName()
                        + " has been removed from the system. They missed their flight to "
                        + assignedFlight.getDestination() + ".\033[0m");

                System.out.println("\033[1;33m🎭 Passenger's comment: \""
                        + funnyComment + "\"\033[0m");

                iterator.remove(); // Remove from a passenger list
                anyMissedFlights = true;
            }
        }

        // If no one missed their flight, print a red message
        if (!anyMissedFlights) {
            System.out.println("\033[1;31mNo passengers missed their flight this cycle.\033[0m");
        }
    }


    /**
     * Simulates the arrival of passengers at the airport.
     */
    private void processPassengerArrival() {
        System.out.println("\nPassenger Arrival Processing:");

        boolean anyArrivals = false;

        for (Passengers passenger : passengers) {
            if (!passenger.hasArrived()
                    && !currentTime.isBefore(passenger.getArrivalTime())) {
                passenger.arriveAtAirport();
                anyArrivals = true;
            }
        }

        if (!anyArrivals) {
            System.out.println("\033[1;31mNo new passenger arrivals this cycle.\033[0m");
        }
    }

    private void processCheckIn() {
        System.out.println("\nCheck-In Processing:");

        boolean anyCheckIns = false;

        for (Passengers passenger : passengers) {
            if (!passenger.hasCheckedIn()) {
                passenger.checkIn();
                anyCheckIns = true; // A passenger has checked in
            }
        }

        // If no passengers checked in, print a message
        if (!anyCheckIns) {
            System.out.println("\033[1;31mNo check-ins this cycle.\033[0m");
        }
    }

    private void processSecurity() {
        System.out.println("\n🔍 Security Checkpoint Processing:");

        boolean anyProcessed = false;

        for (Passengers passenger : passengers) {
            if (!passenger.hasCheckedIn() || passenger.hasClearedSecurity()) {
                continue;
            }

            Airplane assignedFlight = findAirplaneForPassenger(passenger);
            if (assignedFlight == null) {
                System.out.println(passenger.getName()
                        + " does not have a valid assigned flight.");
                continue;
            }

            Airport airport = airplaneAirportMap.get(assignedFlight);
            if (airport != null) {
                int initialWaitTime = passenger.getSecurityWaitTime();
                airport.processPassengerSecurity(passenger); // Process security

                if (initialWaitTime != passenger.getSecurityWaitTime()
                        || passenger.hasClearedSecurity()) {
                    anyProcessed = true;
                }
            }
        }

        if (!anyProcessed) {
            System.out.println("\033[1;31mNo passengers processed at security this cycle.\033[0m");
        }
    }

    private Airplane findAirplaneForPassenger(Passengers passenger) {
        for (Airplane airplane : airplaneAirportMap.keySet()) {
            if (airplane.getDestination().equals(passenger.getAssignedFlight().getDestination())
                    && airplane.getType().equals(passenger.getAssignedFlight().getType())) {
                return airplane;
            }
        }
        return null;
    }

    private void processWaitingPassengers() {
        System.out.println("\n🪑 Passenger Waiting Area Processing:");

        boolean anyWaiting = false;

        for (Passengers passenger : passengers) {
            if (!passenger.hasBoarded()
                    && passenger.hasCheckedIn()
                    && passenger.hasClearedSecurity()) {

                Airplane assignedFlight = findAirplaneForPassenger(passenger);

                // Ensure the passenger has a valid flight
                if (assignedFlight == null) {
                    System.out.println(passenger.getName()
                            + " does not have a valid assigned flight.");
                    continue;
                }

                // Cast to AirplaneModel to access `getModel()`
                AirplaneModel flightModel = (AirplaneModel) assignedFlight;

                // Check if the assigned flight has arrived
                if (!arrivedPlanes.contains(assignedFlight)) {
                    System.out.println(passenger.getName()
                            + " is waiting in the seating area for flight "
                            + flightModel.getModel() + " (" + flightModel.getType()
                            + ") to " + flightModel.getDestination() + ".");
                    anyWaiting = true;
                }
            }
        }

        if (!anyWaiting) {
            System.out.println("\033[1;31mNo passengers "
                    + "are currently waiting in the seating area.\033[0m");
        }
    }

    private void processBoarding() {
        System.out.println("\n🛫 Passenger Boarding Processing:");

        boolean anyBoarding = false;

        for (Passengers passenger : passengers) {
            if (passenger.hasCheckedIn()
                    && passenger.hasClearedSecurity()
                    && !passenger.hasBoarded()) {

                Airplane assignedFlight = findAirplaneForPassenger(passenger);
                if (assignedFlight == null) {
                    System.out.println(passenger.getName()
                            + " does not have a valid assigned flight.");
                    continue;
                }

                // 🚀 **Check if the flight has arrived using FlightScheduler**
                if (!flightScheduler.getArrivedPlanes().contains(assignedFlight)) {
                    System.out.println(passenger.getName() + " cannot board yet—flight "
                            + ((AirplaneModel) assignedFlight).getModel()
                            + " (" + assignedFlight.getType() + ") has not arrived.");
                    continue;
                }

                // Prevent boarding if the flight has already departed
                if (currentTime.isAfter(assignedFlight.getDepartureTime())) {
                    System.out.println("\033[1;31m" + passenger.getName()
                            + " missed their flight to "
                            + assignedFlight.getDestination()
                            + "! Flight has already departed.\033[0m"
                    );
                    continue;
                }

                // Attempt to board
                passenger.boardFlight();

                if (passenger.hasBoarded()) {
                    anyBoarding = true;
                    String departureTime = assignedFlight.getDepartureTime()
                            .format(DateTimeFormatter.ofPattern("HH:mm"));

                    // Add passenger to log but do NOT mark them as in-flight yet
                    boardedPassengersLog.add(passenger.getName()
                            + " has boarded " + assignedFlight.getType()
                            + " to " + assignedFlight.getDestination()
                            + ". Estimated departure: " + departureTime);
                }
            }
        }

        if (!anyBoarding) {
            System.out.println("\033[1;31mNo passengers boarded this cycle.\033[0m");
        }

        // Display list of boarded passengers
        if (!boardedPassengersLog.isEmpty()) {
            System.out.println("\n📝 Boarded Passengers Log:");
            for (String logEntry : boardedPassengersLog) {
                System.out.println(logEntry);
            }
        }
    }

    /**
     * Updates the passenger log to reflect that a passenger is now in flight.
     * This method replaces the previous boarding log entry with an updated
     * status, indicating that the passenger's flight is currently in progress.
     *
     * @param passenger The passenger who has boarded the flight.
     * @param airplane  The airplane the passenger is currently flying on.
     */
    public void updatePassengerLogToInFlight(Passengers passenger, Airplane airplane) {
        String originalEntry = passenger.getName() + " has boarded " + airplane.getType()
                + " to " + airplane.getDestination();

        // Retrieve estimated arrival time
        LocalDateTime arrivalTime = ((AirplaneModel) airplane).getEstimatedArrivalTime();
        String formattedArrivalTime = arrivalTime.format(DateTimeFormatter.ofPattern("HH:mm"));

        String updatedEntry = passenger.getName() + " is on " + airplane.getType()
                + " to " + airplane.getDestination() + " (Expected Arrival: "
                + formattedArrivalTime + ") <<< \033[38;5;214mIN FLIGHT\033[0m";

        // Replace old entry with new 'IN FLIGHT' status
        for (int i = 0; i < boardedPassengersLog.size(); i++) {
            if (boardedPassengersLog.get(i).contains(originalEntry)) {
                boardedPassengersLog.set(i, updatedEntry);
                break;
            }
        }
    }

    private void processPeakHours() {
        System.out.println("\nPeak Hours Processing:");
        for (Airport airport : airports) {
            airport.handlePeakHours(); // Determines if peak hours should be active
        }
    }

    /**
     * Displays the current status of all airports, including gate occupancy,
     * runway usage, and security processing status.
     */
    @Override
    public void displayAirportStatus() {
        System.out.printf(
                "%n│ %-40s │ %-22s │ %-24s │ %-15s │%n",
                "Airport Name",
                "Total Gates (Occupied)",
                "Total Runways (Occupied)",
                "Security Status"
        );
        System.out.println("---------------------------------------"
                + "-------------------------------------"
                + "--------------------------------------");

        for (Airport airport : airports) {
            // Get the count of airplanes currently at this airport
            long occupiedGates = flightScheduler.getArrivedPlanes().stream()
                    .filter(a -> a.getDestination().equalsIgnoreCase(airport.getName()))
                    .count();

            // Count occupied runways (planes that are about to depart or just landed)
            int occupiedRunways = airport.getTotalRunways() - airport.getAvailableRunways();

            // Get security status
            String securityStatus = airport
                    .isSecurityOverloaded() ? "\033[1;31mOverloaded     \033[0m" : "Normal";

            // Print formatted airport status
            System.out.printf("│ %-40s │ %-22s │ %-24s │ %-15s │%n",
                    airport.getName(),
                    airport.getTotalGates() + " (" + occupiedGates + ")",
                    airport.getTotalRunways() + " (" + occupiedRunways + ")",
                    securityStatus);
        }
    }

    private String getRandomCargoType() {
        String[] cargoTypes = {
            "Medical Supplies",
            "Electronics",
            "Automotive Parts",
            "Perishables",
            "Industrial Equipment"
        };
        return cargoTypes[random.nextInt(cargoTypes.length)];
    }

    private int getRandomCargoLoad() {
        int[] cargoLoads = {20, 30, 50, 60, 80}; // Tons
        return cargoLoads[random.nextInt(cargoLoads.length)];
    }

    /**
     * Retrieves the current simulation time as a formatted string.
     *
     * @return The current simulation time.
     */
    @Override
    public String getCurrentSimulationTime() {
        return currentTime.format(timeFormatter);
    }

    /**
     * Retrieves an airport by name.
     *
     * @param name The name of the airport.
     * @return The corresponding Airport object, or null if not found.
     */
    private Airport getAirportByName(String name) {
        for (Airport airport : airports) {
            if (airport.getName().equalsIgnoreCase(name)) {
                return airport;
            }
        }
        return null;
    }

    /**
     * Determines whether all passengers have boarded their assigned flights.
     *
     * @return True if all passengers have boarded, otherwise false.
     */
    private boolean allPassengersBoarded() {
        for (Passengers passenger : passengers) {
            if (!passenger.hasBoarded()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Determines whether all cargo flights have departed.
     *
     * @return True if all cargo flights have departed, otherwise false.
     */
    private boolean allCargoDeparted() {
        for (Airplane airplane : flightScheduler.getArrivedPlanes()) {
            if (airplane.getType().equalsIgnoreCase("CARGO")) {
                return false;
            }
        }
        return true;
    }
}

