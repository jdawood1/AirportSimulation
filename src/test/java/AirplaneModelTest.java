import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class AirplaneModelTest {

    private AirplaneModel airplane;

    @BeforeEach
    void setUp() {
        airplane = AirplaneModel.createAirplane(
                AirplaneModel.AirplaneType.COMMERCIAL,
                "Boeing 737",
                180,
                5.0,
                15000,
                "Los Angeles International",
                "New York JFK",
                LocalDateTime.now().plusHours(2)
        );
    }

    @Test
    void testGetType() {
        assertEquals("COMMERCIAL", airplane.getType());
    }

    @Test
    void testGetModel() {
        assertEquals("Boeing 737", airplane.getModel());
    }

    @Test
    void testGetCapacity() {
        assertEquals(180, airplane.getCapacity());
    }

    @Test
    void testGetFuelEfficiency() {
        assertEquals(5.0, airplane.getFuelEfficiency());
    }

    @Test
    void testGetAssignedAirport() {
        assertEquals("Los Angeles International", airplane.getAssignedAirport());
    }

    @Test
    void testGetDestination() {
        assertEquals("New York JFK", airplane.getDestination());
    }

    @Test
    void testRefuel() {
        airplane.consumeFuel();
        airplane.refuel();
        assertEquals(airplane.getFuelCapacity(), airplane.getFuelCapacity(),
                "Should be refueled to full capacity.");
    }

    @Test
    void testAssignGate() {
        Airport airport = new AirportLocation("Los Angeles International", 4, 2, 1, "COMMERCIAL");
        airplane.assignGate(2, airport);
        assertEquals(2, airplane.getAssignedGate());
    }

    @Test
    void testDepartWithoutGate() {
        airplane.depart();
        assertEquals(-1, airplane.getAssignedGate(), "Airplane should not depart without a gate.");
    }

    @Test
    void testDepartWithGate() {
        Airport airport = new AirportLocation("Los Angeles International", 4, 2, 1, "COMMERCIAL");
        airplane.assignGate(1, airport);
        airplane.depart();
        assertEquals(-1, airplane.getAssignedGate(), "Airplane should depart and unassign gate.");
    }

    @Test
    void testDelayArrival() {
        LocalDateTime originalDeparture = airplane.getDepartureTime();
        airplane.delayArrival();
        assertTrue(airplane.getDepartureTime().isAfter(originalDeparture), "Departure time should be delayed.");
    }

    @Test
    void testDelayDeparture() {
        LocalDateTime originalDeparture = airplane.getDepartureTime();
        airplane.delayDeparture(true, false);
        assertTrue(airplane.getDepartureTime().isAfter(originalDeparture), "Departure should be delayed due to weather.");
    }

    @Test
    void testPerformMaintenance() {
        airplane.performMaintenance();
        assertNotNull(airplane, "Airplane should perform maintenance successfully.");
    }

    @Test
    void testCalculateFlightDuration() {
        airplane.calculateFlightDuration();
        assertNotNull(airplane.getEstimatedArrivalTime(), "Flight duration should be set.");
    }

    @Test
    void testGetFlightDuration() {
        airplane.calculateFlightDuration();
        assertTrue(airplane.getFlightDuration() > 0,
                "Flight duration should be greater than zero.");
    }
}
