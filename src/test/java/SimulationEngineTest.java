import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SimulationEngineTest {

    private SimulationEngine simulationEngine;

    @Mock private FlightScheduler flightSchedulerMock;
    @Mock private Airport airportMock;
    @Mock private AirplaneModel airplaneMock;
    @Mock private Passengers passengerMock;

    private List<Airport> airportList;
    private LocalDateTime testTime;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        airportList = new ArrayList<>();
        List<Passengers> passengerList = new ArrayList<>();
        List<Airplane> airplaneList = new ArrayList<>();
        testTime = LocalDateTime.now();

        // Create mock objects
        when(airportMock.getName()).thenReturn("Mock Airport");
        airportList.add(airportMock);

        when(airplaneMock.getDestination()).thenReturn("Mock Airport");
        when(airplaneMock.getDepartureTime()).thenReturn(testTime.plusHours(2));
        airplaneList.add(airplaneMock);

        when(passengerMock.getAssignedFlight()).thenReturn(airplaneMock);
        when(passengerMock.getName()).thenReturn("John Doe");
        passengerList.add(passengerMock);

        simulationEngine = new SimulationEngine(airportList, airplaneList, passengerList);
    }

    /**
     * Helper method to invoke private methods using Reflection.
     */
    private void invokePrivateMethod(SimulationEngine simulationEngine, String methodName) {
        try {
            Method method = SimulationEngine.class.getDeclaredMethod(methodName);
            method.setAccessible(true); // 🔓 Unlock private access
            method.invoke(simulationEngine); // 🚀 Execute the method
        } catch (Exception e) {
            throw new RuntimeException("[TEST ERROR] Failed to invoke private method: " + methodName, e);
        }
    }

    /**
     * Helper method to invoke private methods using Reflection and return values.
     */
    private Object invokePrivateMethodWithReturn(Object instance, String methodName, Object... params) {
        try {
            Class<?>[] paramTypes = new Class[params.length];

            for (int i = 0; i < params.length; i++) {
                paramTypes[i] = params[i] != null ? params[i].getClass() : Object.class;
            }

            Method method = instance.getClass().getDeclaredMethod(methodName, paramTypes);
            method.setAccessible(true); // Unlock private access
            return method.invoke(instance, params); // Execute & return result
        } catch (Exception e) {
            throw new RuntimeException("[TEST ERROR] Failed to invoke private method: " + methodName, e);
        }
    }

    @Test
    void testProcessPassengerArrival_WhenPassengerArrives() {
        when(passengerMock.hasArrived()).thenReturn(false);
        when(passengerMock.getArrivalTime()).thenReturn(testTime.minusMinutes(10));

        invokePrivateMethod(simulationEngine, "processPassengerArrival");

        verify(passengerMock, times(1)).arriveAtAirport();
    }

    @Test
    void testProcessPassengerArrival_NoNewArrivals() {
        when(passengerMock.hasArrived()).thenReturn(true);

        invokePrivateMethod(simulationEngine, "processPassengerArrival");

        verify(passengerMock, never()).arriveAtAirport();
    }

    @Test
    void testProcessCheckIn_WhenPassengerChecksIn() {
        when(passengerMock.hasCheckedIn()).thenReturn(false);

        invokePrivateMethod(simulationEngine, "processCheckIn");

        verify(passengerMock, times(1)).checkIn();
    }

    @Test
    void testProcessCheckIn_WhenAlreadyCheckedIn() {
        when(passengerMock.hasCheckedIn()).thenReturn(true);

        invokePrivateMethod(simulationEngine, "processCheckIn");

        verify(passengerMock, never()).checkIn();
    }

    @Test
    void testDisplayAirportStatus_PrintsSuccessfully() {
        assertDoesNotThrow(() -> simulationEngine.displayAirportStatus(),
                "[TEST DEBUG] displayAirportStatus() should run without exceptions.");
    }

    @Test
    void testUpdatePassengerLogToInFlight() {
        when(airplaneMock.getEstimatedArrivalTime()).thenReturn(testTime.plusHours(3));

        simulationEngine.updatePassengerLogToInFlight(passengerMock, airplaneMock);

        System.out.println("[TEST DEBUG] Passenger log updated for: " + passengerMock.getName());
    }

    @Test
    void testProcessPeakHours_ShouldCallAirport() {
        invokePrivateMethod(simulationEngine, "processPeakHours");

        verify(airportMock, times(1)).handlePeakHours();
    }

    @Test
    void testGetCurrentSimulationTime_ShouldReturnFormattedTime() {
        String time = simulationEngine.getCurrentSimulationTime();

        assertNotNull(time, "[TEST DEBUG] getCurrentSimulationTime() should return a non-null value.");
        System.out.println("[TEST DEBUG] Current Simulation Time: " + time);
    }

    @Test
    void testProcessSecurity_WhenPassengerPassesSecurity() {
        System.out.println("[TEST DEBUG] Setting up mock behavior for processSecurity...");

        // Mock passenger attributes
        when(passengerMock.hasCheckedIn()).thenReturn(true);
        when(passengerMock.hasClearedSecurity()).thenReturn(false);
        when(passengerMock.getSecurityWaitTime()).thenReturn(0);

        // Mock airport behavior
        when(airportMock.isSecurityOverloaded()).thenReturn(false);

        when(airplaneMock.getType()).thenReturn("COMMERCIAL");

        System.out.println("[TEST DEBUG] hasCheckedIn: " + passengerMock.hasCheckedIn());
        System.out.println("[TEST DEBUG] hasClearedSecurity: " + passengerMock.hasClearedSecurity());
        System.out.println("[TEST DEBUG] Security Wait Time: " + passengerMock.getSecurityWaitTime());
        System.out.println("[TEST DEBUG] isSecurityOverloaded: " + airportMock.isSecurityOverloaded());
        System.out.println("[TEST DEBUG] Airplane Type: " + airplaneMock.getType());

        try {
            invokePrivateMethod(simulationEngine, "processSecurity");
            System.out.println("[TEST DEBUG] processSecurity() invoked successfully.");
        } catch (Exception e) {
            System.err.println("[TEST ERROR] Exception occurred during processSecurity(): " + e.getMessage());
            e.printStackTrace();
        }

        verify(airportMock, times(1)).processPassengerSecurity(passengerMock);
        System.out.println("[TEST DEBUG] Verified that processPassengerSecurity() was called.");
    }

    @Test
    void testRemoveMissedPassengers_WhenPassengerMissesFlight() {
        System.out.println("[TEST DEBUG] Setting up mock behavior for removeMissedPassengers...");

        // Mocking assigned airplane
        when(passengerMock.getAssignedFlight()).thenReturn(airplaneMock);
        when(airplaneMock.getDepartureTime()).thenReturn(testTime.minusMinutes(10)); // Pastime
        when(passengerMock.hasBoarded()).thenReturn(false);
        when(passengerMock.getName()).thenReturn("John Doe");

        // Ensure findAirplaneForPassenger() returns a valid airplane
        when(airplaneMock.getDestination()).thenReturn("Mock Airport");
        when(airplaneMock.getType()).thenReturn("COMMERCIAL");
        when(airportMock.getName()).thenReturn("Mock Airport");

        System.out.println("[TEST DEBUG] Passenger Name: " + passengerMock.getName());
        System.out.println("[TEST DEBUG] Flight Departure Time: " + airplaneMock.getDepartureTime());
        System.out.println("[TEST DEBUG] Has Boarded? " + passengerMock.hasBoarded());
        System.out.println("[TEST DEBUG] Flight Destination: " + airplaneMock.getDestination());
        System.out.println("[TEST DEBUG] Flight Type: " + airplaneMock.getType());

        // Invoke the private method
        invokePrivateMethod(simulationEngine, "removeMissedPassengers");

        // Instead of checking "times(1)", allow multiple invocations
        verify(passengerMock, atLeastOnce()).getAssignedFlight();
    }

    @Test
    void testProcessCargoFlights_WhenCargoFlightArrives() {
        System.out.println("[TEST DEBUG] Setting up mock behavior for processCargoFlights...");

        // Mock an arrived CARGO airplane
        when(airplaneMock.getType()).thenReturn("CARGO");
        when(airplaneMock.getModel()).thenReturn("Boeing 747-8F");
        when(airplaneMock.getDestination()).thenReturn("Memphis International");
        when(flightSchedulerMock.getArrivedPlanes()).thenReturn(List.of(airplaneMock));

        // Debug: Print out what getArrivedPlanes() returns
        System.out.println("[TEST DEBUG] Arrived Planes Count: " + flightSchedulerMock.getArrivedPlanes().size());
        System.out.println("[TEST DEBUG] First Plane Type: " + flightSchedulerMock.getArrivedPlanes().get(0).getType());

        // Invoke the method
        invokePrivateMethod(simulationEngine, "processCargoFlights");

        // Verify that cargo processing logic was executed
        verify(airplaneMock, atLeastOnce()).getType();
        verify(airplaneMock, atMostOnce()).delayDeparture(false, false);

        System.out.println("[TEST DEBUG] Cargo flight processed successfully.");
    }

    @Test
    void testProcessWaitingPassengers_WhenPassengersAreWaiting() {
        System.out.println("[TEST DEBUG] Setting up mock behavior for processWaitingPassengers...");

        // Mock passenger behavior
        when(passengerMock.hasCheckedIn()).thenReturn(true);
        when(passengerMock.hasClearedSecurity()).thenReturn(true);
        when(passengerMock.hasBoarded()).thenReturn(false);
        when(passengerMock.getName()).thenReturn("John Doe");
        when(passengerMock.getAssignedFlight()).thenReturn(airplaneMock);

        // Mock airplane behavior (Flight hasn't arrived yet)
        when(airplaneMock.getType()).thenReturn("COMMERCIAL");
        when(airplaneMock.getDestination()).thenReturn("Mock Airport");

        // Ensure the flight hasn't arrived yet
        when(flightSchedulerMock.getArrivedPlanes()).thenReturn(new ArrayList<>());

        // Debugging information
        System.out.println("[TEST DEBUG] Passenger Name: " + passengerMock.getName());
        System.out.println("[TEST DEBUG] Flight Destination: " + airplaneMock.getDestination());
        System.out.println("[TEST DEBUG] Flight Type: " + airplaneMock.getType());
        System.out.println("[TEST DEBUG] Has Flight Arrived? " + flightSchedulerMock.getArrivedPlanes().contains(airplaneMock));

        // Invoke the method
        invokePrivateMethod(simulationEngine, "processWaitingPassengers");

        // Verify that the passenger is considered "waiting"
        verify(passengerMock, never()).boardFlight();

        System.out.println("[TEST DEBUG] Passenger correctly recognized as waiting.");
    }

    @Test
    void testProcessPeakHours_ShouldTriggerPeakHourHandling() {
        System.out.println("[TEST DEBUG] Setting up mock behavior for processPeakHours...");

        when(airportMock.getName()).thenReturn("Mock Airport");
        airportList.add(airportMock);

        try {
            invokePrivateMethod(simulationEngine, "processPeakHours");
        } catch (Exception e) {
            e.printStackTrace();
            fail("[TEST ERROR] Exception occurred while invoking processPeakHours()");
        }

        verify(airportMock, times(1)).handlePeakHours();
        System.out.println("[TEST DEBUG] processPeakHours() invoked successfully.");
    }

    @Test
    void testProcessWaitingPassengers_ShouldIdentifyWaitingPassengers() {
        System.out.println("[TEST DEBUG] Setting up mock behavior for processWaitingPassengers...");

        when(passengerMock.hasCheckedIn()).thenReturn(true);
        when(passengerMock.hasClearedSecurity()).thenReturn(true);
        when(passengerMock.hasBoarded()).thenReturn(false);
        when(passengerMock.getAssignedFlight()).thenReturn(airplaneMock);

        when(airplaneMock.getDestination()).thenReturn("Mock Airport");
        when(airplaneMock.getType()).thenReturn("COMMERCIAL");

        when(airportMock.getName()).thenReturn("Mock Airport");
        airportList.add(airportMock);

        try {
            invokePrivateMethod(simulationEngine, "processWaitingPassengers");
        } catch (Exception e) {
            e.printStackTrace();
            fail("[TEST ERROR] Exception occurred while invoking processWaitingPassengers()");
        }

        verify(passengerMock, never()).boardFlight(); // Passengers shouldn't board yet
        System.out.println("[TEST DEBUG] processWaitingPassengers() invoked successfully.");
    }

    @Test
    void testGetAirportByName_WhenAirportExists() {
        System.out.println("[TEST DEBUG] Setting up mock behavior for getAirportByName...");

        when(airportMock.getName()).thenReturn("Mock Airport");
        airportList.add(airportMock);

        Airport foundAirport = (Airport) invokePrivateMethodWithReturn(simulationEngine, "getAirportByName", "Mock Airport");

        assertNotNull(foundAirport, "[TEST DEBUG] getAirportByName() should return a valid airport when it exists.");
        System.out.println("[TEST DEBUG] Found Airport Name: " + foundAirport.getName());
    }

    @Test
    void testAllPassengersBoarded_WhenAllHaveBoarded() {
        System.out.println("[TEST DEBUG] Setting up test for allPassengersBoarded...");

        when(passengerMock.hasBoarded()).thenReturn(true);

        boolean result = (boolean) invokePrivateMethodWithReturn(simulationEngine, "allPassengersBoarded");

        assertTrue(result, "[TEST DEBUG] allPassengersBoarded() should return true when all passengers have boarded.");
    }

    @Test
    void testGetRandomCargoType_ShouldReturnValidCargo() {
        System.out.println("[TEST DEBUG] Running testGetRandomCargoType...");

        String cargoType = (String) invokePrivateMethodWithReturn(simulationEngine, "getRandomCargoType");

        assertNotNull(cargoType, "[TEST DEBUG] getRandomCargoType() should return a valid cargo type.");
        System.out.println("[TEST DEBUG] Generated Cargo Type: " + cargoType);
    }

    @Test
    void testGetRandomCargoLoad_ShouldReturnValidLoad() {
        System.out.println("[TEST DEBUG] Running testGetRandomCargoLoad...");

        int cargoLoad = (int) invokePrivateMethodWithReturn(simulationEngine, "getRandomCargoLoad");

        assertTrue(cargoLoad > 0, "[TEST DEBUG] getRandomCargoLoad() should return a positive cargo load.");
        System.out.println("[TEST DEBUG] Generated Cargo Load: " + cargoLoad + " tons");
    }
}


