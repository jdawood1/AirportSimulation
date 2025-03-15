import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Random;


/**
 * Represents an airport location with gates, runways, security checkpoints,
 * and peak hour management.
 */
public class AirportLocation implements Airport {
    private final String name;
    private final int totalGates;
    private final int totalRunways;
    private final int totalSecurityCheckpoints;
    private final PriorityQueue<Integer> availableGates;
    private final PriorityQueue<Integer> availableRunways;
    private final AirplaneModel.AirplaneType airportType;
    private final Queue<AirplaneModel> securityQueue;
    private boolean isPeakHour;
    private static final Random random = new Random();

    /**
     * Constructs an AirportLocation with specific details.
     *
     * @param name                     The name of the airport.
     * @param totalGates               The total number of gates.
     * @param totalRunways             The total number of runways.
     * @param totalSecurityCheckpoints The total number of security checkpoints.
     * @param airportTypeStr           The airport type as a string.
     */
    public AirportLocation(String name, int totalGates, int totalRunways,
                           int totalSecurityCheckpoints, String airportTypeStr) {
        this.name = name;
        this.totalGates = totalGates;
        this.totalRunways = totalRunways;
        this.totalSecurityCheckpoints = totalSecurityCheckpoints;
        this.availableGates = new PriorityQueue<>();
        this.availableRunways = new PriorityQueue<>();
        this.securityQueue = new PriorityQueue<>();
        this.airportType = AirplaneModel.AirplaneType.valueOf(airportTypeStr.toUpperCase());

        // Initialize gates and runways
        for (int i = 1; i <= totalGates; i++) {
            availableGates.add(i);
        }
        for (int i = 1; i <= totalRunways; i++) {
            availableRunways.add(i);
        }

        this.isPeakHour = false;
    }

    @Override
    public int getNextAvailableGate() {
        if (availableGates.isEmpty()) {
            System.out.println(name + " has no available gates.");
            System.out.println(name + " currently has "
                    + getAvailableRunways() + " available runways.");
            return -1;
        }

        int gate = availableGates.poll();
        updateGateUsage(); // Track gate usage dynamically
        System.out.println(name + " assigned Gate " + gate
                + ". Remaining available gates: " + availableGates.size());
        return gate;
    }

    @Override
    public void releaseGate(int gateNumber) {
        if (gateNumber > 0 && gateNumber <= totalGates && !availableGates.contains(gateNumber)) {
            availableGates.add(gateNumber);
            updateGateUsage(); // Track gate usage dynamically
            System.out.println("Gate " + gateNumber + " at " + name + " is now available.");
        }
    }

    /**
     * Returns the airport type (COMMERCIAL, CARGO, PRIVATE_JET).
     *
     * @return The airport type.
     */
    public AirplaneModel.AirplaneType getAirportType() {
        return this.airportType;
    }

    @Override
    public boolean requestRunway(AirplaneModel airplane) {
        if (availableRunways.isEmpty()) {
            System.out.println("🚦 No available runways for "
                    + airplane.getType() + " at " + name + ". Flight delayed.");
            return false;
        }

        int assignedRunway = availableRunways.poll();
        updateRunwayUsage(); // Track runway usage dynamically
        System.out.println("✅ " + airplane.getType()
                + " granted access to runway " + assignedRunway + " at " + name);
        return true;
    }

    @Override
    public void releaseRunway(AirplaneModel airplane) {
        if (availableRunways.size() < totalRunways) {
            int freedRunway = totalRunways - availableRunways.size();
            availableRunways.add(freedRunway);
            updateRunwayUsage(); // Track runway usage dynamically
            System.out.println("🛫 " + airplane.getType()
                    + " has vacated runway " + freedRunway + " at " + name);
        }
    }

    @Override
    public void handlePeakHours() {
        isPeakHour = random.nextBoolean(); // 50% chance of peak hours each cycle
        System.out.println(name + " is now "
                + (isPeakHour ? "in peak hours! Expect delays." : "operating normally."));
    }

    @Override
    public void processPassengerSecurity(Passengers passenger) {
        if (!passenger.hasCheckedIn()) {
            System.out.println(passenger.getName()
                    + " cannot go through security without checking in.");
            return;
        }

        int additionalDelay = 0;

        // Apply peak hour delay only if it's the first time during peak hours
        if (isPeakHour && !passenger.hasReceivedPeakHourDelay()) {
            additionalDelay = 60;
            passenger.setHasReceivedPeakHourDelay(true);
        }

        int securityWait = passenger.getSecurityWaitTime() - 30 + additionalDelay;

        if (securityWait > 0) {
            passenger.setSecurityWaitTime(securityWait);
            System.out.println(passenger.getName()
                    + " is delayed at security at " + this.name
                    + ". Remaining wait: " + securityWait + " minutes.");
        } else {
            passenger.clearSecurity();
            System.out.println(passenger.getName()
                    + " has cleared security at " + this.name);
        }
    }

    /**
     * Marks a runway as occupied by removing it from the list of available runways.
     * If a runway is available, it is assigned and removed from the queue.
     * A message is printed indicating which runway is now occupied.
     */
    public void markRunwayOccupied() {
        if (!availableRunways.isEmpty()) {
            int usedRunway = availableRunways.poll();
            System.out.println("🚦 Runway " + usedRunway
                    + " at " + name + " is now occupied.");
        }
    }

    @Override
    public void updateGateUsage() {
        // for testing ...
    }

    @Override
    public void updateRunwayUsage() {
        // for testing ...
    }

    @Override
    public boolean isSecurityOverloaded() {
        return isPeakHour || securityQueue.size() >= totalSecurityCheckpoints;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getTotalGates() {
        return totalGates;
    }

    @Override
    public int getTotalRunways() {
        return totalRunways;
    }

    @Override
    public int getAvailableRunways() {
        return availableRunways.size();
    }

}

