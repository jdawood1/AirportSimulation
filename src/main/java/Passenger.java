import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

/**
 * Represents a passenger in the airport simulation.
 * Passengers check in, go through security, and board their assigned flights.
 * This class also simulates potential delays in passenger processing.
 */
public class Passenger implements Passengers {
    private final String name;
    private boolean checkInIssue;
    private boolean boardingIssue;
    private boolean hasCheckedIn;
    private boolean hasClearedSecurity;
    private boolean hasBoarded;
    private int checkInWaitTime;
    private int securityWaitTime;
    private int boardingWaitTime;
    private final AirplaneModel assignedFlight;
    private static final Random random = new Random();
    private boolean hasArrived = false;
    private boolean hasReceivedPeakHourDelay = false;
    private final LocalDateTime arrivalTime;

    /**
     * Constructs a Passenger with a name and assigned flight.
     * The passenger's arrival time is set at least 2 hours before departure.
     *
     * @param name           The passenger's name.
     * @param assignedFlight The flight assigned to the passenger.
     */
    public Passenger(String name, AirplaneModel assignedFlight) {
        this.name = name;
        this.assignedFlight = assignedFlight;
        this.simulateProcessingDelays();
        this.hasCheckedIn = false;
        this.hasClearedSecurity = false;
        this.hasBoarded = false;
        this.boardingWaitTime = random.nextInt(30) + 5;
        this.securityWaitTime = random.nextInt(30) + 5;
        this.hasArrived = false;
        this.hasReceivedPeakHourDelay = false;

        // Set arrival time at least 2 hours before departure
        this.arrivalTime = assignedFlight.getDepartureTime().minusHours(2);
    }

    @Override
    public void arriveAtAirport() {
        hasArrived = true;

        // Get assigned flight details
        AirplaneModel assignedFlight = getAssignedFlight();
        String airplaneType = assignedFlight.getType();
        String destination = assignedFlight.getDestination();
        String arrivalTime = getArrivalTime().format(DateTimeFormatter.ofPattern("HH:mm"));
        String departureTime = assignedFlight
                .getDepartureTime()
                .format(DateTimeFormatter
                .ofPattern("HH:mm"));

        // Print passenger arrival details
        System.out.printf("👤 Passenger: %-15s │ ✈ Airplane: %-12s │ "
                    + "🛫 Destination: %-40s │ 🕒 Arrival Time: %-5s │ ⏳ Departure Time: %-5s%n",
                getName(), airplaneType, destination, arrivalTime, departureTime);
    }

    @Override
    public void checkIn() {
        if (!hasCheckedIn) {
            if (checkInIssue && checkInWaitTime > 0) {
                System.out.println(name
                        + " is delayed at check-in. Remaining wait: "
                        + checkInWaitTime + " minutes.");
                checkInWaitTime = Math.max(0, checkInWaitTime - 30); // Reduce delay per cycle
            } else {
                System.out.println(name + " has successfully checked in.");
                hasCheckedIn = true;
            }
        }
    }

    @Override
    public void boardFlight() {
        if (!hasCheckedIn || !hasClearedSecurity) {
            System.out.println(name
                    + " cannot board without completing check-in and security.");
            return;
        }

        if (!hasBoarded) {
            if (boardingIssue && boardingWaitTime > 0) {
                System.out.println(name
                        + " is delayed at boarding. Remaining wait: "
                        + boardingWaitTime + " minutes.");
                boardingWaitTime = Math.max(0, boardingWaitTime - 30);
            } else {
                System.out.println(name + " has boarded the flight.");
                hasBoarded = true;
            }
        }
    }

    /**
     * Simulates random processing delays for check-in, security, and boarding.
     * This method randomly assigns delays to the passenger's processing times.
     */
    private void simulateProcessingDelays() {
        this.checkInIssue = random.nextBoolean();
        boolean securityIssue = random.nextBoolean();
        this.boardingIssue = random.nextBoolean();
        if (checkInIssue) {
            this.checkInWaitTime += random.nextInt(20) + 10;
        }
        if (securityIssue) {
            this.securityWaitTime += random.nextInt(20) + 10;
        }
        if (boardingIssue) {
            this.boardingWaitTime += random.nextInt(20) + 10;
        }
    }

    /**
     * Checks if the passenger has received a delay due to peak-hour congestion.
     *
     * @return True if the passenger has been delayed, false otherwise.
     */
    public boolean hasReceivedPeakHourDelay() {
        return hasReceivedPeakHourDelay;
    }

    /**
     * Sets whether the passenger has been delayed due to peak-hour congestion.
     *
     * @param hasReceivedPeakHourDelay True if the passenger has received the delay.
     */
    public void setHasReceivedPeakHourDelay(boolean hasReceivedPeakHourDelay) {
        this.hasReceivedPeakHourDelay = hasReceivedPeakHourDelay;
    }

    /**
     * Retrieves the passenger's arrival time.
     *
     * @return The arrival time of the passenger.
     */
    public LocalDateTime getArrivalTime() {
        return arrivalTime;
    }

    @Override
    public boolean hasClearedSecurity() {
        return hasClearedSecurity;
    }

    @Override
    public boolean hasBoarded() {
        return hasBoarded;
    }

    @Override
    public int getSecurityWaitTime() {
        return securityWaitTime;
    }

    @Override
    public void setSecurityWaitTime(int time) {
        this.securityWaitTime = time;
    }

    @Override
    public AirplaneModel getAssignedFlight() {
        return assignedFlight;
    }

    @Override
    public boolean hasCheckedIn() {
        return hasCheckedIn;
    }

    @Override
    public void clearSecurity() {
        hasClearedSecurity = true;
    }

    @Override
    public boolean hasArrived() {
        return hasArrived;
    }

    @Override
    public String getName() {
        return name;
    }

}
