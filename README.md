# Airport Simulation—Design Patterns

## Initial Plan
This project will implement three GoF design patterns in an airport simulation:

### **1. Singleton Pattern—Airport Control System**
The Singleton pattern will be used to manage a central **Airport Control System**,
ensuring only one instance of this system exists.
This will help regulate airport resources such as runways, gates, and passenger flow efficiently.

### **2. Observer Pattern—Flight Status Notifications**
The Observer pattern will be implemented to notify relevant components when an **airplane's status changes**.
This will be useful for passengers, gate agents, and refueling teams waiting for real-time flight status updates.

### **3. Strategy Pattern—Passenger Check-in and Boarding Process**
The Strategy pattern will be used for handling **different check-in and boarding strategies**,
such as priority boarding, self-check-in kiosks, and standard check-in counters.
This will allow for flexibility in passenger processing.

## Setup
- Clone the repository
- Run `./gradlew build` to build the project
- Run `./gradlew run` to execute the simulation