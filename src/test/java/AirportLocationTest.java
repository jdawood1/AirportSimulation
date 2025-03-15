import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;

class AirportLocationTest {
    private AirportLocation airport;
    private AirplaneModel mockAirplane;

    @BeforeEach
    void setUp() {
        airport = new AirportLocation("Test Airport", 5, 3, 2, "COMMERCIAL");

        // Mock a passenger and an airplane
        Passengers mockPassenger = Mockito.mock(Passengers.class);
        mockAirplane = Mockito.mock(AirplaneModel.class);

        Mockito.when(mockPassenger.hasCheckedIn()).thenReturn(true);
        Mockito.when(mockPassenger.getSecurityWaitTime()).thenReturn(30);
        Mockito.when(mockAirplane.getType()).thenReturn("COMMERCIAL");
    }

    @Test
    void testGetName() {
        assertEquals("Test Airport", airport.getName());
    }

    @Test
    void testGetTotalGates() {
        assertEquals(5, airport.getTotalGates());
    }

    @Test
    void testGetTotalRunways() {
        assertEquals(3, airport.getTotalRunways());
    }

    @Test
    void testGetNextAvailableGate() {
        int gate = airport.getNextAvailableGate();
        assertTrue(gate > 0 && gate <= 5);  // Should be between 1-5
    }

    @Test
    void testReleaseGate() {
        int gate = airport.getNextAvailableGate();
        assertTrue(gate > 0);
        airport.releaseGate(gate);
        assertEquals(gate, airport.getNextAvailableGate()); // Gate should be available again
    }

    @Test
    void testRequestRunway() {
        assertTrue(airport.requestRunway(mockAirplane)); // Runway should be available initially
    }

    @Test
    void testReleaseRunway() {
        airport.requestRunway(mockAirplane);
        airport.releaseRunway(mockAirplane);
        assertEquals(3, airport.getAvailableRunways()); // The Runway should be freed again
    }

    @Test
    void testHandlePeakHours() {
        // Since it is random, just check if method runs without errors
        assertDoesNotThrow(() -> airport.handlePeakHours());
    }

    @Test
    void testProcessPassengerSecurity() {
        // Arrange
        Passengers mockPassenger = Mockito.mock(Passengers.class);
        AirportLocation airportLocation = new AirportLocation("Test Airport", 5, 2, 2, "COMMERCIAL");

        // Mock conditions
        Mockito.when(mockPassenger.hasCheckedIn()).thenReturn(true);
        Mockito.when(mockPassenger.getSecurityWaitTime()).thenReturn(10);

        // Act
        airportLocation.processPassengerSecurity(mockPassenger);

        // Assert
        Mockito.verify(mockPassenger, times(1)).clearSecurity(); // Ensure clearSecurity() is actually called
    }

    @Test
    void testMarkRunwayOccupied() {
        airport.markRunwayOccupied();
        assertTrue(airport.getAvailableRunways() < 3); // At least one runway should be occupied
    }

    @Test
    void testIsSecurityOverloaded() {
        assertFalse(airport.isSecurityOverloaded()); // Initially, security should not be overloaded
    }

    @Test
    void testUpdateGateUsage() {
        assertDoesNotThrow(() -> airport.updateGateUsage()); // Ensure no errors
    }

    @Test
    void testUpdateRunwayUsage() {
        assertDoesNotThrow(() -> airport.updateRunwayUsage()); // Ensure no errors
    }
}

