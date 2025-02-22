# Airport Simulation Project

## Assignment 5a/b - Design Patterns

**Version: February 21, 2025**

Welcome to the **Airport Simulation Project**,
an advanced Java-based airport simulation that integrates key software design principles and patterns.
This project is structured using **Gradle** for build automation,
**JUnit** for testing, and **GitHub Actions** for CI/CD.

---

## Table of Contents

1. [Project Overview](#project-overview)
2. [Design Patterns Implemented](#design-patterns-implemented)
3. [Implementation Details](#implementation-details)
4. [Functional Requirements Checklist](#functional-requirements-checklist)
5. [Gradle Setup](#gradle-setup)
6. [Testing & Code Quality](#testing--code-quality)
7. [GitHub Actions CI/CD](#github-actions-cicd)
8. [Build & Run Instructions](#build--run-instructions)
9. [Challenges Faced & Solutions](#challenges-faced--solutions)
10. [Screencast & GitHub Repository](#screencast--github-repository)
11. [Resources Used](#resources-used)

---

## Project Overview

This project simulates an **airport environment**, where flights are dynamically managed, passengers go through
check-in, security, and boarding, and the system processes arrivals and departures over timed cycles. The project
leverages key **design patterns** to ensure modularity and maintainability.

### Key Features:

- **Flight Scheduling & Management**: Flights are assigned departure times, delays, and maintenance.
- **Passenger Handling**: Check-in, security clearance, and boarding processes.
- **Automated Simulation Cycles**: The simulation progresses in 30-minute intervals.
- **System Logging & Debugging**: Clear terminal outputs for simulation tracking.

---

# Design Patterns Implemented

This project applies **three core design patterns** to optimize airport operations, flight scheduling, and passenger
management.

---

## 1. Singleton Pattern (Creational)

**Class:** `SimulationEngine`  
**Purpose:** Ensures that only **one instance** of the **SimulationEngine** manages the entire airport system. This
prevents inconsistencies in simulation cycles and airport resource management.

### Implementation:

```java
public class SimulationEngine implements Simulation {
    private static SimulationEngine instance;
    private final List<Airport> airports;
    private final List<Passengers> passengers;
    private final FlightScheduler flightScheduler;
    private LocalDateTime currentTime;

    private SimulationEngine(List<Airport> airports, List<Airplane> airplanes, List<Passengers> passengers) {
        this.airports = new ArrayList<>(airports);
        this.passengers = new ArrayList<>(passengers);
        this.flightScheduler = new FlightScheduler(this, passengers);
        this.currentTime = LocalDateTime.now();

        for (Airplane airplane : airplanes) {
            flightScheduler.scheduleFlight(airplane, airplane.getDepartureTime());
        }
    }

    public static synchronized SimulationEngine getInstance(List<Airport> airports, List<Airplane> airplanes, List<Passengers> passengers) {
        if (instance == null) {
            instance = new SimulationEngine(airports, airplanes, passengers);
        }
        return instance;
    }
}
```

### Why Singleton?

- Ensures only one simulation instance controls all airport events, avoiding duplicate processing.
- Prevents multiple conflicting simulation states from being created.
- Provides global access to the simulation instance across the project.

---

## 2. Observer Pattern (Behavioral)

**Class:** `FlightScheduler` & `Passengers`  
**Purpose:** **Passengers (observers)** receive **real-time updates** when their flight status changes (e.g., delays,
cancellations, departure announcements).

### Implementation:

```java
public interface FlightObserver {
    void updateFlightStatus(String flightNumber, String status);
}

public class Passenger implements Passengers, FlightObserver {
    private final String name;
    private final AirplaneModel assignedFlight;

    public Passenger(String name, AirplaneModel assignedFlight) {
        this.name = name;
        this.assignedFlight = assignedFlight;
    }

    @Override
    public void updateFlightStatus(String flightNumber, String status) {
        if (assignedFlight.getModel().equals(flightNumber)) {
            System.out.println("📢 Passenger " + name + ", your flight " + flightNumber + " status updated: " + status);
        }
    }
}
```

### Publisher (Subject) - `FlightScheduler`

```java
public class FlightScheduler {
    private final List<FlightObserver> observers = new ArrayList<>();

    public void addObserver(FlightObserver observer) {
        observers.add(observer);
    }

    public void notifyObservers(String flightNumber, String status) {
        for (FlightObserver observer : observers) {
            observer.updateFlightStatus(flightNumber, status);
        }
    }

    public void processDepartures() {
        for (Airplane airplane : arrivedPlanes) {
            notifyObservers(airplane.getModel(), "Departed");
        }
    }
}
```

### Why Observer?

- Passengers automatically receive flight status updates (delays, boarding, departure).
- Reduces tight coupling between the flight system and passengers, improving scalability.
- Supports future features like email notifications or mobile alerts.

---

## 3. Strategy Pattern (Behavioral)

**Class:** `AirportDataProcessor`  
**Purpose:**
**Dynamically selects a data processing strategy** for handling airport operations, depending on the type of airport
(Commercial, Cargo, or Private Jet).

### Implementation:

```java
public interface DataProcessingStrategy {
    void process(Airport airport);
}

public class CommercialAirportProcessing implements DataProcessingStrategy {
    @Override
    public void process(Airport airport) {
        System.out.println("📊 Processing commercial airport: " + airport.getName());
    }
}

public class CargoAirportProcessing implements DataProcessingStrategy {
    @Override
    public void process(Airport airport) {
        System.out.println("🚛 Processing cargo hub: " + airport.getName());
    }
}
```

### Strategy Selection in `AirportDataProcessor`

```java
public class AirportDataProcessor {
    private DataProcessingStrategy strategy;

    public void setStrategy(DataProcessingStrategy strategy) {
        this.strategy = strategy;
    }

    public void executeStrategy(Airport airport) {
        if (strategy != null) {
            strategy.process(airport);
        }
    }
}
```

### Usage Example in Simulation:

```java
AirportDataProcessor processor = new AirportDataProcessor();

if (airport.getType().equalsIgnoreCase("COMMERCIAL")) {
    processor.setStrategy(new CommercialAirportProcessing());
} else if (airport.getType().equalsIgnoreCase("CARGO")) {
    processor.setStrategy(new CargoAirportProcessing());
}

processor.executeStrategy(airport);
```

### Why Strategy?

- Allows dynamic selection of airport processing logic without modifying core logic.
- Keeps airport operations flexible, supporting new airport types in the future.
- Encapsulates complex airport behavior into independent strategies for cleaner code.

---

## Summary

| **Design Pattern** | **Class Implemented**            | **Purpose**                                                                                              |
|--------------------|----------------------------------|----------------------------------------------------------------------------------------------------------|
| **Singleton**      | `SimulationEngine`               | Ensures a **single simulation instance** manages all flights and airport operations.                     |
| **Observer**       | `FlightScheduler` & `Passengers` | Notifies passengers **when their flight status changes** (delays, cancellations, departures).            |
| **Strategy**       | `AirportDataProcessor`           | Implements **different data-processing strategies** for **Commercial, Cargo, and Private Jet airports**. |

---

## Functional Requirements Checklist

| Requirement                                  | Status        |
|----------------------------------------------|---------------|
| Flights scheduled dynamically                | ✔ Implemented |
| Passenger check-in and boarding              | ✔ Implemented |
| Observer pattern for flight updates          | ✔ Implemented |
| Strategy pattern for airport data processing | ✔ Implemented |
| Singleton pattern for airport control system | ✔ Implemented |
| Simulation progresses in cycles              | ✔ Implemented |
| Automated build system using Gradle          | ✔ Implemented |
| Unit testing with 70%+ coverage              | ✔ Implemented |
| Checkstyle and SpotBugs integrated           | ✔ Implemented |

---

## Extra Credit Checklist

| Requirement                                                                                      | Status        |
|--------------------------------------------------------------------------------------------------|---------------|
| Different types of airplanes with varying capacities and fuel efficiencies                       | ✔ Implemented |
| Airplanes arrive at the airport and need to be assigned gates                                    | ✔ Implemented |
| Airplanes depart for pre-determined destinations after a set amount of time                      | ✔ Implemented |
| Simulate refueling and maintenance procedures                                                    | ✔ Implemented |
| Passengers arrive at the airport for different flights                                           | ✔ Implemented |
| Passengers check-in, go through security, and board their assigned flights                       | ✔ Implemented |
| Simulate delays or issues with passenger processing                                              | ✔ Implemented |
| Limited number of gates, runways, and security checkpoints                                       | ✔ Implemented |
| Manage the allocation of resources to avoid conflicts and delays                                 | ✔ Implemented |
| Implement strategies for handling peak hours with increased traffic                              | ✔ Implemented |
| Run the simulation in cycles (e.g., each cycle represents 30 minutes)                            | ✔ Implemented |
| Display the status of the airport (arrivals, departures, gate assignments) at each cycle         | ✔ Implemented |
| Generate reports or logs of airport activity (e.g., average waiting times, resource utilization) | ✔ Implemented |

---

##  Gradle Setup

This project is managed using **Gradle** and includes:

- **JUnit 5** for unit testing.
- **Checkstyle** and **SpotBugs** for static analysis.
- **JaCoCo** for test coverage reports.
- **GitHub Actions** for continuous integration.

### Running the Project:

```sh
./gradlew build
./gradlew run
```

### Running Tests:

```sh
./gradlew test
./gradlew jacocoTestReport
```

---

## Testing & Code Quality

- **JUnit 5 Tests:** Ensure over **70% test coverage**.
- **JaCoCo Report:** Confirms covered and uncovered code.
- **Static Analysis:**
    - **Checkstyle** enforces coding standards.
    - **SpotBugs** identifies potential issues.

---

## GitHub Actions CI/CD

A **GitHub Actions workflow** automates:

1. **Building the project** (`gradle build`)
2. **Running tests** (`gradle test`)
3. **Executing Checkstyle & SpotBugs** (`gradle check`)

This ensures **continuous integration** with every commit.

---

## Build & Run Instructions

### Clone the Repository

```sh
git clone https://github.com/JDaw2024/AirportSimulation
cd AirportSimulation
```

### Build & Run

```sh
./gradlew build
./gradlew run
```

### Run Tests & Generate Reports

```sh
./gradlew test
./gradlew jacocoTestReport
open build/reports/jacoco/test/html/index.html
```

---

## Challenges Faced & Solutions

### **1. Managing Passenger Notifications**

**Issue:** Ensuring passengers receive real-time flight updates.
**Solution:** Implemented **Observer Pattern** with a list of **FlightObservers**.

### **2. Thread-Safe Singleton Implementation**

**Issue:** Preventing race conditions in `SimulationEngine`.
**Solution:** Used a **lazy-loaded synchronized Singleton** approach.

### **3. Ensuring Design Pattern Integration**

**Issue:** Making design patterns work together effectively.
**Solution:** Used **Observer + Strategy**, allowing real-time passenger updates with flexible processing strategies.

---

## Screencast & GitHub Repository

### GitHub Repository:

[GitHub Link](https://github.com/JDaw2024/AirportSimulation)

### Screencast:

[Video Demo](link) `[N/A]`

---

## Resources Used

1. **Design Patterns: Elements of Reusable Object-Oriented Software** (GoF)
2. **Oracle Java Documentation**
3. **Baeldung Java Tutorials**
4. **Gradle Documentation**

---

## Submission Checklist

✔ **GitHub Repository** with code, Gradle, and README.

✔ **GitHub Actions** enabled with CI/CD.

✔ **JUnit 5 Tests** with 70%+ coverage.

✔ **JaCoCo, Checkstyle, and SpotBugs Reports** included.

❌ **Screencast Demonstrating Project Execution.**

✔ **README with Detailed Documentation.**

---

# Thank You & Happy Coding 🤙

