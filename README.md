# RideWise 🚗💨

RideWise is a simplified, console-based ride-sharing application (inspired by Uber/Ola) designed in Java. The project demonstrates core Low-Level Design (LLD) concepts, Object-Oriented Design (OOD) principles, and SOLID design patterns.

---

## 🌟 Features

- **Ride Booking**: Riders can request rides between pickup and drop locations.
- **Driver Matching**: Dynamically find nearby drivers using flexible matching strategies:
  - **Nearest Driver Strategy**: Matches riders with the closest available driver.
  - **Least Active Driver Strategy**: Promotes fairness by matching drivers with the fewest completed rides.
- **Dynamic Fare Calculation**: Flexible fare pricing mechanisms:
  - **Default Fare Strategy**: Standard distance-based pricing.
  - **Peak Hour Fare Strategy**: Surge pricing applied based on demand/peak timing.
- **Trip Lifecycle Tracking**: Real-time status updates across different ride states (`REQUESTED`, `ACCEPTED`, `IN_PROGRESS`, `COMPLETED`, `CANCELLED`).
- **Fare Receipts**: Generates clean fare summary receipts at the end of each trip.

---

## 🏗️ Project Architecture & Design Patterns

RideWise heavily utilizes key Design Patterns and SOLID principles:

- **Strategy Pattern**: Used for both ride matching (`RideMatchingStrategy`) and fare pricing (`FareStrategy`), allowing algorithms to be swapped seamlessly at runtime without modifying client code.
- **Service Layer Pattern**: Decouples business logic (`RiderService`, `DriverService`, `RideService`) from data models and UI layer.
- **Single Responsibility Principle (SRP)**: Each class serves a single, dedicated purpose (e.g., ID generation, fare calculation, model representations).

---

## 📂 File & Package Structure

```text
RideWise/
├── docs/
│   ├── Class_Model.md
│   ├── Object_Relationships.md
│   ├── Requirements.md
│   └── SOLID_Reflection.md
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/airtribe/ridewise/
│   │   │       ├── Main.java
│   │   │       ├── exception/
│   │   │       │   └── NoDriverAvailableException.java
│   │   │       ├── model/
│   │   │       │   ├── Driver.java
│   │   │       │   ├── FareReceipt.java
│   │   │       │   ├── Ride.java
│   │   │       │   ├── Rider.java
│   │   │       │   └── RideStatus.java
│   │   │       ├── service/
│   │   │       │   ├── DriverService.java
│   │   │       │   ├── RideService.java
│   │   │       │   └── RiderService.java
│   │   │       ├── strategy/
│   │   │       │   ├── DefaultFareStrategy.java
│   │   │       │   ├── FareStrategy.java
│   │   │       │   ├── LeastActiveDriverStrategy.java
│   │   │       │   ├── NearestDriverStrategy.java
│   │   │       │   ├── PeakHourFareStrategy.java
│   │   │       │   └── RideMatchingStrategy.java
│   │   │       └── util/
│   │   │           └── IdGenerator.java
│   │   └── resources/
│   └── test/
│       └── java/
├── pom.xml
└── README.md
```

---

## 🚀 Getting Started

### Prerequisites

- **Java Development Kit (JDK)**: Version 11 or higher
- **Maven**: Version 3.6+ (or use an IDE with built-in Maven support like IntelliJ IDEA)

### Building the Project

1. **Clone the repository**:
   ```bash
   git clone https://github.com/your-username/RideWise.git
   cd RideWise
   ```

2. **Compile using Maven**:
   ```bash
   mvn clean compile
   ```

3. **Run the Application**:
   ```bash
   mvn exec:java -Dexec.mainClass="com.airtribe.ridewise.Main"
   ```
   *Or simply run `Main.java` directly from your IDE.*

---

## 📜 Documentation

Detailed design reflections and architecture diagrams can be found in the [`docs/`](./docs) folder:
- **`Requirements.md`**: Problem statement and functional specifications.
- **`Class_Model.md`**: Low-level class design and responsibility breakdowns.
- **`SOLID_Reflection.md`**: Reflection on how SOLID principles are applied across the codebase.
- **`Object_Relationships.md`**: Diagrams explaining relationship cardinalities (associations, aggregations, compositions).
