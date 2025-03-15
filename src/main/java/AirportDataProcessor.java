import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Processes and manages airport-related data, including
 * airports, airplanes, and passengers.
 */
public class AirportDataProcessor {
    private final List<Airport> airports;
    private final List<Airplane> airplanes;
    private final List<Passengers> passengers;
    private static final Random random = new Random();

    /**
     * Initializes the airport data processor and loads all necessary data.
     */
    public AirportDataProcessor() {
        this.airports = new ArrayList<>();
        this.airplanes = new ArrayList<>();
        this.passengers = new ArrayList<>();
        loadData();
    }

    /**
     * Loads predefined airport, airplane, and passenger data.
     */
    private void loadData() {
        loadAirports();
        loadAirplanes();
        loadPassengers();
    }

    /**
     * Loads predefined airports.
     */
    private void loadAirports() {
        // Commercial Airports (Major Passenger Hubs)
        airports.add(new AirportLocation(
                "Atlanta Hartsfield-Jackson", 5, 3, 2, "COMMERCIAL"));
        airports.add(new AirportLocation(
                "Los Angeles International", 4, 3, 2, "COMMERCIAL"));
        airports.add(new AirportLocation(
                "Chicago O'Hare", 6, 4, 3, "COMMERCIAL"));
        airports.add(new AirportLocation(
                "Dallas/Fort Worth", 5, 3, 2, "COMMERCIAL"));
        airports.add(new AirportLocation(
                "Denver International", 6, 4, 3, "COMMERCIAL"));
        airports.add(new AirportLocation(
                "New York John F. Kennedy International", 6, 4, 3, "COMMERCIAL"));
        airports.add(new AirportLocation(
                "New York LaGuardia", 4, 3, 2, "COMMERCIAL"));
        airports.add(new AirportLocation(
                "Miami International", 5, 3, 2, "COMMERCIAL"));
        airports.add(new AirportLocation(
                "Philadelphia International", 5, 3, 2, "COMMERCIAL"));
        airports.add(new AirportLocation(
                "Phoenix Sky Harbor International", 5, 3, 2, "COMMERCIAL"));
        airports.add(new AirportLocation(
                "San Francisco International", 6, 4, 3, "COMMERCIAL"));
        airports.add(new AirportLocation(
                "Seattle-Tacoma International", 5, 3, 2, "COMMERCIAL"));
        airports.add(new AirportLocation(
                "Washington Reagan National", 4, 3, 2, "COMMERCIAL"));
        airports.add(new AirportLocation(
                "Charlotte Douglas International", 5, 3, 2, "COMMERCIAL"));
        airports.add(new AirportLocation(
                "Houston George Bush Intercontinental", 6, 4, 3, "COMMERCIAL"));
        airports.add(new AirportLocation(
                "Las Vegas Harry Reid International", 4, 3, 2, "COMMERCIAL"));

        // Cargo Hubs (FedEx, UPS, and Large Freight Operations)
        airports.add(new AirportLocation(
                "Memphis International (FedEx Hub)", 4, 2, 1, "CARGO"));
        airports.add(new AirportLocation(
                "Louisville (UPS Hub)", 4, 2, 1, "CARGO"));

        // Private Jet Airports (Smaller Executive & VIP Travel Facilities)
        airports.add(new AirportLocation(
                "Teterboro Airport (Private)", 2, 1, 1, "PRIVATE_JET"));
        airports.add(new AirportLocation(
                "Las Vegas Harry Reid International", 4, 3, 2, "PRIVATE_JET"));
        airports.add(new AirportLocation(
                "Miami International", 5, 3, 2, "PRIVATE_JET"));
        airports.add(new AirportLocation(
                "Los Angeles International", 4, 3, 2, "PRIVATE_JET"));
        airports.add(new AirportLocation(
                "Denver International", 6, 4, 3, "PRIVATE_JET"));

        System.out.println("✔ Loaded " + airports.size() + " airports.");
    }


    /**
     * Loads predefined airplanes with random assignments.
     */
    private void loadAirplanes() {
        int totalAirplanes = 20;
        System.out.println("✔ Loading " + totalAirplanes + " airplanes.");
        for (int i = 0; i < totalAirplanes; i++) {
            AirplaneModel.AirplaneType type = getRandomAirplaneType();

            Airport assignedAirport = getRandomAirport(type);
            Airport destinationAirport = getRandomDestination(assignedAirport);

            if (destinationAirport == null) {
                assert assignedAirport != null;
                System.out.println("[DEBUG]: No valid destination found for "
                        + assignedAirport.getName());
                continue;
            }

            double fuelCapacity = getFuelCapacityByType(type);
            double fuelEfficiency = getFuelEfficiencyByType(type);

            // Assign different capacities based on an airplane type
            int capacity;
            if (type == AirplaneModel.AirplaneType.COMMERCIAL) {
                capacity = getRandomCommercialCapacity(); // High seat count
            } else if (type == AirplaneModel.AirplaneType.PRIVATE_JET) {
                capacity = getRandomPrivateJetCapacity(); // Lower seat count
            } else { // CARGO aircraft
                capacity = getRandomCargoCapacity(); // Cargo measured in tons
            }

            // Create and add a new airplane
            assert assignedAirport != null;
            AirplaneModel airplane = AirplaneModel.createAirplane(
                    type,
                    getRandomModel(type),
                    capacity,
                    fuelEfficiency,
                    fuelCapacity,
                    assignedAirport.getName(),
                    destinationAirport.getName(),
                    getRandomDepartureTime()
            );

            airplanes.add(airplane);

            String capacityLabel = (
                    type == AirplaneModel.AirplaneType.CARGO) ? "tons cargo" : "seats";
            System.out.println("✈ Created " + airplane.getType() + " (" + airplane.getCapacity()
                + " " + capacityLabel + ") from " + assignedAirport.getName()
                + " to " + destinationAirport.getName() + "."
            );
            System.out.println("Fuel Capacity: " + airplane.getFuelCapacity()
                + " gallons, Fuel Efficiency: "
                + airplane.getFuelEfficiency()
                + " gallons per mile."
            );
        }
    }

    /**
     * Returns fuel efficiency (gallons per mile) based on airplane type.
     */
    private double getFuelEfficiencyByType(AirplaneModel.AirplaneType type) {
        switch (type) {
            case COMMERCIAL:
                return 5.0 + random.nextDouble() * 1.5; // Range: 5.0 - 6.5
            case CARGO:
                return 3.5 + random.nextDouble() * 1.2; // Range: 3.5 - 4.7
            case PRIVATE_JET:
                return 6.0 + random.nextDouble() * 2.0; // Range: 6.0 - 8.0
            default:
                throw new IllegalArgumentException("Unknown airplane type: " + type);
        }

    }

    private int getRandomCommercialCapacity() {
        int[] capacities = {150, 180, 200, 220}; // Higher seat range
        return capacities[random.nextInt(capacities.length)];
    }

    private int getRandomPrivateJetCapacity() {
        int[] capacities = {10, 15, 20, 25}; // Lower seat range
        return capacities[random.nextInt(capacities.length)];
    }

    private int getRandomCargoCapacity() {
        int[] cargoCapacities = {20, 30, 50, 60}; // Tons of cargo
        return cargoCapacities[random.nextInt(cargoCapacities.length)];
    }

    /**
     * Returns a random airport from the list.
     */
    private Airport getRandomAirport(AirplaneModel.AirplaneType airplaneType) {
        List<Airport> filteredAirports = new ArrayList<>();

        for (Airport airport : airports) {
            if (airport instanceof AirportLocation) {
                AirportLocation airportLocation = (AirportLocation) airport;

                if (airportLocation.getAirportType() == airplaneType) {
                    filteredAirports.add(airportLocation);
                }
            }
        }

        return filteredAirports.isEmpty() ? null : filteredAirports.get(
                random.nextInt(filteredAirports.size()));
    }

    /**
     * Ensures that the assigned destination is different from the departure airport.
     */
    private Airport getRandomDestination(Airport assignedAirport) {
        List<Airport> availableDestinations = new ArrayList<>(airports);
        availableDestinations.remove(assignedAirport);

        if (availableDestinations.isEmpty()) {
            return null; // No valid destinations available
        }

        return availableDestinations.get(
                random.nextInt(availableDestinations.size()));
    }

    /**
     * Returns fuel capacity based on an airplane type.
     */
    private double getFuelCapacityByType(AirplaneModel.AirplaneType type) {
        switch (type) {
            case COMMERCIAL:
                return 12000 + random.nextInt(6000); // 12,000 - 18,000 gallons
            case CARGO:
                return 15000 + random.nextInt(5000); // 15,000 - 20,000 gallons
            case PRIVATE_JET:
                return 2000 + random.nextInt(2000); // 2,000 - 4,000 gallons
            default:
                return 5000; // Default
        }
    }

    private AirplaneModel.AirplaneType getRandomAirplaneType() {
        AirplaneModel.AirplaneType[] types = AirplaneModel.AirplaneType.values();
        return types[random.nextInt(types.length)];
    }

    private String getRandomModel(AirplaneModel.AirplaneType type) {
        String[] selectedModels;

        switch (type) {
            case COMMERCIAL:
                selectedModels = new String[]{
                    "Boeing 737",
                    "Boeing 747",
                    "Airbus A320",
                    "Airbus A350",
                    "McDonnell Douglas MD-80"
                };
                break;
            case CARGO:
                selectedModels = new String[]{
                    "Boeing 747-8F",
                    "Airbus A330-200F",
                    "McDonnell Douglas MD-11F",
                    "Lockheed C-130",
                    "Antonov An-124"
                };
                break;
            case PRIVATE_JET:
                selectedModels = new String[]{
                    "Gulfstream G650",
                    "Bombardier Global 7500",
                    "Cessna Citation X",
                    "Embraer Phenom 300",
                    "Dassault Falcon 7X"
                };
                break;
            default:
                throw new IllegalArgumentException("Unknown airplane type: " + type);
        }

        return selectedModels[random.nextInt(selectedModels.length)];
    }

    /**
     * Loads passengers and assigns them to flights.
     */
    private void loadPassengers() {
        String[] names = {
            "John Doe", "Jane Smith", "Mike Johnson", "Emily Davis", "Robert Brown",
            "Sarah Wilson", "David Martinez", "Lisa White", "Kevin Harris", "Jessica Brown",
            "James Anderson", "Olivia Taylor", "Ethan Thomas", "Sophia Moore", "Daniel Lewis",
            "Ava Jackson", "Benjamin Walker", "Mia Hall", "Alexander Young", "Emma Allen"
        };

        if (airplanes.isEmpty()) {
            System.out.println("No airplanes available. Skipping passenger loading.");
            return;
        }

        int flightIndex = 0;
        for (String name : names) {
            AirplaneModel assignedFlight = (AirplaneModel) airplanes.get(flightIndex);

            // Ensure passengers are NOT assigned to Cargo Planes
            while (assignedFlight.getType().equalsIgnoreCase("CARGO")) {
                flightIndex = (flightIndex + 1) % airplanes.size();
                assignedFlight = (AirplaneModel) airplanes.get(flightIndex);
            }

            passengers.add(new Passenger(name, assignedFlight));

            // Move to the next available flight
            flightIndex = (flightIndex + 1) % airplanes.size();
        }

        System.out.println("✔ Loaded " + passengers.size() + " passengers.");
    }

    /**
     * Generates a departure time between 1 and 2 hours from the simulation start,
     * ensuring alignment with 15-minute increments.
     */
    private LocalDateTime getRandomDepartureTime() {
        LocalDateTime now = LocalDateTime.now();

        // Minimum delay before departure: 1 hour (60 minutes)
        // Maximum delay before departure: 2 hours (120 minutes)
        int minMinutes = 60;
        int maxMinutes = 120;

        // Generate a random value in 15-minute increments between 60 and 120 minutes
        int randomMinutes = minMinutes + (
                random.nextInt((maxMinutes - minMinutes) / 15 + 1) * 15);

        LocalDateTime departureTime = now.plusMinutes(randomMinutes);

        // Align departure time to the nearest 15-minute mark (00, 15, 30, 45)
        int minute = departureTime.getMinute();
        int roundedMinutes = (minute / 15) * 15;

        return departureTime.withMinute(roundedMinutes).withSecond(0);
    }

    /**
     * Returns an unmodifiable list of loaded airports.
     *
     * @return List of airports (read-only).
     */
    public List<Airport> getAirports() {
        return Collections.unmodifiableList(airports);
    }

    /**
     * Returns an unmodifiable list of loaded airplanes.
     *
     * @return List of airplanes (read-only).
     */
    public List<Airplane> getAirplanes() {
        return Collections.unmodifiableList(airplanes);
    }

    /**
     * Returns an unmodifiable list of loaded passengers.
     *
     * @return List of passengers (read-only).
     */
    public List<Passengers> getPassengers() {
        return Collections.unmodifiableList(passengers);
    }

    /**
     * Displays loaded data.
     */
    public void displayLoadedData() {
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        // Display Airport Data
        System.out.printf("%n%-40s | %-10s | %-10s%n", "Airport Name", "Gates", "Runways");
        System.out.println("----------------------------------------------------------------");
        airports.forEach(a ->
            System.out.printf("%-40s | %-10d | %-10d%n",
                a.getName(), a.getTotalGates(), a.getTotalRunways())
        );

        // Display Airplane Data
        System.out.printf(
            "%n%-15s | %-25s | %-20s | %-20s | %-40s | %-30s%n",
            "Type", "Model", "Departure Time", "Arrival Time",
            "Assigned Airport", "Destination"
        );
        System.out.println("---------------------------------"
            + "-----------------------------------------"
            + "-------------------------------------------"
            + "------------------------------------------"
        );
        airplanes.forEach(a -> {
            AirplaneModel airplane = (AirplaneModel) a;
            String formattedDeparture = airplane.getDepartureTime().format(formatter);
            String formattedArrival = airplane.getEstimatedArrivalTime().format(formatter);

            System.out.printf("%-15s | %-25s | %-20s | %-20s | %-40s | %-30s%n",
                airplane.getType(), airplane.getModel(), formattedDeparture, formattedArrival,
                airplane.getAssignedAirport(), airplane.getDestination());
        });

        // Display Passenger Data
        System.out.printf(
            "%n%-22s | %-15s | %-25s | %-40s | %-30s%n",
            "Passenger Name", "Airplane Type", "Assigned Flight",
            "Assigned Airport", "Destination"
        );
        System.out.println("---------------------------------------------------------"
            + "------------------------------------------------------------------");

        passengers.forEach(p -> {
            AirplaneModel assignedFlight = p.getAssignedFlight();
            System.out.printf("%-22s | %-15s | %-25s | %-40s | %-30s%n",
                p.getName(),
                assignedFlight.getType(),
                assignedFlight.getModel(),
                assignedFlight.getAssignedAirport(),
                assignedFlight.getDestination());
        });
    }
}
