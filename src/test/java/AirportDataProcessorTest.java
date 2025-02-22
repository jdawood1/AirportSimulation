import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AirportDataProcessorTest {
    private AirportDataProcessor dataProcessor;

    @BeforeEach
    void setUp() {
        dataProcessor = new AirportDataProcessor(); // Load data before each test
    }

    @Test
    void testLoadAirports() {
        List<Airport> airports = dataProcessor.getAirports();
        assertNotNull(airports, "Airport list should not be null.");
        assertFalse(airports.isEmpty(), "Airport list should not be empty.");
        assertTrue(airports.size() >= 20, "At least 20 airports should be loaded.");
    }

    @Test
    void testLoadAirplanes() {
        List<Airplane> airplanes = dataProcessor.getAirplanes();
        assertNotNull(airplanes, "Airplane list should not be null.");
        assertFalse(airplanes.isEmpty(), "Airplane list should not be empty.");
        assertTrue(airplanes.size() >= 20, "At least 20 airplanes should be loaded.");

        // Verify airplanes are assigned to valid airports
        for (Airplane airplane : airplanes) {
            assertNotNull(airplane.getAssignedAirport(), "Each airplane should have an assigned airport.");
            assertNotNull(airplane.getDestination(), "Each airplane should have a destination.");
        }
    }

    @Test
    void testLoadPassengers() {
        List<Passengers> passengers = dataProcessor.getPassengers();
        assertNotNull(passengers, "Passenger list should not be null.");
        assertFalse(passengers.isEmpty(), "Passenger list should not be empty.");
        assertTrue(passengers.size() > 10, "There should be more than 10 passengers.");

        // Ensure each passenger is assigned a flight
        for (Passengers passenger : passengers) {
            assertNotNull(passenger.getAssignedFlight(), "Each passenger should have an assigned flight.");
            assertNotEquals("CARGO", passenger.getAssignedFlight().getType(),
                    "Passengers should not be assigned to cargo flights.");
        }
    }

    @Test
    void testAirplaneFuelCapacity() {
        List<Airplane> airplanes = dataProcessor.getAirplanes();

        for (Airplane airplane : airplanes) {
            AirplaneModel airplaneModel = (AirplaneModel) airplane;
            assertTrue(airplaneModel.getFuelCapacity() > 0, "Fuel capacity should be greater than 0.");
        }
    }

    @Test
    void testAirplaneFuelEfficiency() {
        List<Airplane> airplanes = dataProcessor.getAirplanes();

        for (Airplane airplane : airplanes) {
            AirplaneModel airplaneModel = (AirplaneModel) airplane;
            assertTrue(airplaneModel.getFuelEfficiency() > 0, "Fuel efficiency should be greater than 0.");
        }
    }

    @Test
    void testImmutabilityOfReturnedLists() {
        List<Airport> airports = dataProcessor.getAirports();
        List<Airplane> airplanes = dataProcessor.getAirplanes();
        List<Passengers> passengers = dataProcessor.getPassengers();

        assertThrows(UnsupportedOperationException.class, () -> airports.add(null),
                "Returned airport list should be immutable.");
        assertThrows(UnsupportedOperationException.class, () -> airplanes.add(null),
                "Returned airplane list should be immutable.");
        assertThrows(UnsupportedOperationException.class, () -> passengers.add(null),
                "Returned passenger list should be immutable.");
    }
}

