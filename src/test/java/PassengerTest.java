import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PassengerTest {
    private Passenger passenger;
    private AirplaneModel mockFlight;

    @BeforeEach
    void setUp() {
        // Mock an AirplaneModel for testing
        mockFlight = Mockito.mock(AirplaneModel.class);
        when(mockFlight.getDepartureTime()).thenReturn(LocalDateTime.now().plusHours(3));

        // Create Passenger instance
        passenger = new Passenger("John Doe", mockFlight);
    }

    @Test
    void testPassengerInitialization() {
        assertEquals("John Doe", passenger.getName());
        assertEquals(mockFlight, passenger.getAssignedFlight());
        assertFalse(passenger.hasCheckedIn());
        assertFalse(passenger.hasClearedSecurity());
        assertFalse(passenger.hasBoarded());
        assertFalse(passenger.hasArrived());
    }

    @Test
    void testArriveAtAirport() {
        passenger.arriveAtAirport();
        assertTrue(passenger.hasArrived());
    }

    @Test
    void testCheckInWithDelay() {
        // Simulate delay
        passenger.checkIn();
        passenger.checkIn();  // Try again after a cycle
        assertTrue(passenger.hasCheckedIn());
    }

    @Test
    void testClearSecurity() {
        passenger.clearSecurity();
        assertTrue(passenger.hasClearedSecurity());
    }

    @Test
    void testBoardFlightWithoutCheckIn() {
        passenger.clearSecurity(); // Skipping check-in
        passenger.boardFlight();
        assertFalse(passenger.hasBoarded()); // Should fail to board
    }

    @Test
    void testBoardFlightWithoutClearingSecurity() {
        passenger.checkIn(); // Skipping security clearance
        passenger.boardFlight();
        assertFalse(passenger.hasBoarded()); // Should fail to board
    }

    @Test
    void testHasReceivedPeakHourDelay() {
        passenger.setHasReceivedPeakHourDelay(true);
        assertTrue(passenger.hasReceivedPeakHourDelay());
    }

    @Test
    void testSecurityWaitTimeModification() {
        passenger.setSecurityWaitTime(20);
        assertEquals(20, passenger.getSecurityWaitTime());
    }
}

