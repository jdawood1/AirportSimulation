import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AirportManagerTest {
    private AirportManager airportManager;
    private Airport mockAirport1;
    private Airport mockAirport2;

    @BeforeEach
    void setUp() {
        // Mock Airport instances
        mockAirport1 = Mockito.mock(Airport.class);
        when(mockAirport1.getName()).thenReturn("JFK International");

        mockAirport2 = Mockito.mock(Airport.class);
        when(mockAirport2.getName()).thenReturn("Los Angeles International");

        // Create AirportManager with mocked airports
        List<Airport> airportList = Arrays.asList(mockAirport1, mockAirport2);
        airportManager = new AirportManager(airportList);
    }

    @Test
    void testGetAirportByName_Found() {
        Airport result = airportManager.getAirportByName("JFK International");
        assertNotNull(result, "Airport should be found.");
        assertEquals(mockAirport1, result, "Should return the correct airport instance.");
    }

    @Test
    void testGetAirportByName_CaseInsensitive() {
        Airport result = airportManager.getAirportByName("los angeles international");
        assertNotNull(result, "Airport should be found (case insensitive).");
        assertEquals(mockAirport2, result, "Should match regardless of case.");
    }

    @Test
    void testGetAirportByName_NotFound() {
        Airport result = airportManager.getAirportByName("Nonexistent Airport");
        assertNull(result, "Should return null for a non-existent airport.");
    }

}

