import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Represents an airplane with details about its type, fuel efficiency,
 * departure, and assigned airport.
 */
public class AirplaneModel implements Airplane {

    public enum AirplaneType {
        COMMERCIAL, CARGO, PRIVATE_JET
    }

    private final AirplaneType type;
    private final String model;
    private final int capacity;
    private final double fuelEfficiency;  // Gallons per mile
    private final double fuelCapacity;    // Maximum fuel capacity
    private double currentFuel;           // Current fuel level
    private double currentFuelLevel;
    private int assignedGate = -1;
    private final String assignedAirportName;
    private final String destination;
    private LocalDateTime departureTime;
    private LocalDateTime estimatedArrivalTime;
    private int flightDuration;
    private boolean isAirborne = false;
    private static final Random random = new Random();
    private boolean requiresRefueling;

    private final Map<Airplane, Airport> airplaneAirportMap = new HashMap<>();

    /**
     * Constructs an AirplaneModel instance.
     *
     * @param type                The type of airplane.
     * @param model               The model of the airplane.
     * @param capacity            The seating or cargo capacity.
     * @param fuelEfficiency      Fuel efficiency in gallons per mile.
     * @param fuelCapacity        Maximum fuel capacity.
     * @param assignedAirportName The assigned airport.
     * @param destination         The flight destination.
     * @param departureTime       The scheduled departure time.
     */
    private AirplaneModel(AirplaneType type, String model, int capacity, double fuelEfficiency,
                          double fuelCapacity, String assignedAirportName, String destination,
                          LocalDateTime departureTime) {
        this.type = type;
        this.model = model;
        this.capacity = capacity;
        this.fuelEfficiency = fuelEfficiency;
        this.fuelCapacity = fuelCapacity;
        this.currentFuelLevel = fuelCapacity; // Start with a full tank
        this.assignedAirportName = assignedAirportName;
        this.destination = destination;
        this.departureTime = departureTime;
        this.requiresRefueling = false;
    }

    /**
     * Creates an instance of an AirplaneModel.
     *
     * @return A new AirplaneModel object.
     */
    public static AirplaneModel createAirplane(AirplaneType type, String model, int capacity,
                                               double fuelEfficiency, double fuelCapacity,
                                               String assignedAirportName, String destination,
                                               LocalDateTime departureTime) {
        AirplaneModel airplane = new AirplaneModel(type, model, capacity, fuelEfficiency,
                fuelCapacity, assignedAirportName, destination, departureTime);
        airplane.calculateFlightDuration(); // Ensures arrival time is set
        return airplane;
    }


    /**
     * Calculates flight duration based on randomized distances.
     */
    public void calculateFlightDuration() {
        int flightTimeMinutes = 60 + random.nextInt(181);
        flightDuration = flightTimeMinutes; // Ensure flightDuration is assigned
        estimatedArrivalTime = departureTime.plusMinutes(flightTimeMinutes);
        isAirborne = true;
    }

    @Override
    public int getFlightDuration() {
        return flightDuration;
    }

    /**
     * Returns the model of the airplane.
     *
     * @return The airplane model.
     */
    public String getModel() {
        return this.model;
    }

    /**
     * Checks if the plane needs refueling before departure.
     *
     * @return True if refueling is needed, false otherwise.
     */
    public boolean needsRefueling() {
        return currentFuel < (fuelCapacity * 0.2); // If below 20%, refueling is required
    }

    /**
     * Refuels the airplane to full capacity.
     */
    public void refuel() {
        if (!requiresRefueling) {
            return;
        }

        this.currentFuelLevel = fuelCapacity;
        this.requiresRefueling = false;
        System.out.println("⛽ " + model + " refueled to " + fuelCapacity + " gallons.");
    }

    /**
     * Calculates estimated fuel usage.
     *
     * @return The fuel usage in gallons.
     */
    private double calculateFuelUsage() {
        double[] estimatedDistances = {500, 1000, 1500, 2000, 2500}; // Miles between airports
        double distance = estimatedDistances[random.nextInt(estimatedDistances.length)];
        return distance / fuelEfficiency;
    }

    public boolean hasSufficientFuel() {
        return !requiresRefueling && currentFuelLevel >= calculateFuelUsage();
    }

    @Override
    public void assignGate(int gateNumber, Airport airport) {
        this.assignedGate = gateNumber;
        System.out.println(model + " (" + type + ") assigned to gate " + gateNumber
                + " at " + airport.getName());
    }

    @Override
    public void depart() {
        if (assignedGate == -1) {
            System.out.println("❌ " + model + " (" + type + ") cannot depart—no gate assigned.");
            return;
        }
        if (requiresRefueling) {
            System.out.println("⛽ " + model + " requires refueling before departure.");
            return;
        }

        double fuelNeeded = calculateFuelUsage();
        if (fuelNeeded > currentFuelLevel) {
            System.out.println("🚨 " + model
                    + " does not have enough fuel to reach " + destination + "!");
            requiresRefueling = true;
            return;
        }

        currentFuelLevel -= fuelNeeded;
        System.out.println("✈ " + model + " (" + type + ") is departing from Gate " + assignedGate
                + " to " + destination + ".");
        System.out.println("Remaining Fuel: " + currentFuelLevel + " gallons.");

        assignedGate = -1;

        // Ensure the airport releases the runway properly
        if (airplaneAirportMap.containsKey(this)) {
            Airport airport = airplaneAirportMap.get(this);
            airport.releaseRunway(this);
        }
    }

    /**
     * Delay Arrival at the Airport.
     */
    @Override
    public void delayArrival() {
        int delayMinutes = 30 + random.nextInt(31);
        departureTime = departureTime.plusMinutes(delayMinutes);

        System.out.println(model + " (" + type + ") arrival delayed by "
                + departureTime.format(DateTimeFormatter.ofPattern("HH:mm")) + ".");
    }

    /**
     * Delay Departure & Return Delay Duration.
     */
    @Override
    public void delayDeparture(boolean weatherDelay, boolean congestionDelay) {
        // Random delay between 20-60 minutes
        int lastDelayMinutes = 20 + random.nextInt(41);
        departureTime = departureTime.plusMinutes(lastDelayMinutes);

        String reason = weatherDelay ? "Weather conditions (storm, fog, etc.)" :
                congestionDelay ? "Heavy airport congestion" : "⚠ Passenger boarding delays";

        System.out.println(model + " (" + type + ") departure delayed by " + lastDelayMinutes
                + " minutes due to: " + reason + ". New departure time: "
                + departureTime.format(DateTimeFormatter.ofPattern("HH:mm")) + ".");

    }

    /**
     * Calculates the fuel required for a flight based on distance.
     */
    private double calculateFuelForFlight() {
        double estimatedFlightTimeHours = getFlightTimeEstimate();
        return estimatedFlightTimeHours * fuelEfficiency; // Fuel needed = Time * Fuel burn rate
    }

    /**
     * Estimates the flight time based on the airport distance.
     */
    private double getFlightTimeEstimate() {
        return 2 + random.nextDouble() * 3; // Estimate between 2-5 hours
    }

    /**
     * Consumes fuel based on flight duration.
     */
    public void consumeFuel() {
        double fuelBurned = calculateFuelForFlight();
        currentFuelLevel -= fuelBurned;
        if (currentFuelLevel < 0) {
            currentFuelLevel = 0;
        }
        System.out.println("⛽ " + model + " (" + type + ") burned " + fuelBurned
                + " gallons. Remaining fuel: " + currentFuelLevel + " gallons.");
    }

    @Override
    public void performMaintenance() {
        System.out.println("🛠️ " + model + " (" + type + ") is undergoing maintenance.");
    }

    @Override
    public String getAssignedAirport() {
        return assignedAirportName;
    }

    @Override
    public String getType() {
        return type.toString();
    }

    @Override
    public int getCapacity() {
        return capacity;
    }

    @Override
    public double getFuelEfficiency() {
        return fuelEfficiency;
    }

    @Override
    public int getAssignedGate() {
        return assignedGate;
    }

    @Override
    public String getDestination() {
        return destination;
    }

    @Override
    public LocalDateTime getDepartureTime() {
        return departureTime;
    }

    public double getFuelCapacity() {
        return fuelCapacity;
    }

    public LocalDateTime getEstimatedArrivalTime() {
        return estimatedArrivalTime;
    }

    public boolean isAirborne() {
        return isAirborne;
    }

}
