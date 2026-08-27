# 🎬 Step-by-Step Presentation & Live Demo Guide

This guide provides a structured, phase-by-phase script for presenting **Domain-Driven Design (DDD)**, **Hexagonal Architecture (Ports & Adapters)**, **Herberto Graça's Explicit Architecture**, and **ArchUnit Architecture Enforcement** using the Satellite Management System codebase.

---

## 🛠️ Surefire HTML Test Report Setup & Viewing

Running `mvn clean test` or `mvn surefire-report:report` automatically generates a comprehensive HTML test report:

```bash
# 1. Run full test suite & generate HTML report
mvn clean test

# OR explicitly execute the surefire report goal
mvn surefire-report:report
```

- **Report Location**: `target/site/surefire-report.html`
- **Viewing in Browser**: Open `target/site/surefire-report.html` in Chrome/Safari to see a visual summary of all **37 passing unit, integration, and ArchUnit architecture tests**.

---

## ❓ Frequently Asked Q&A: Does ArchUnit Run Pre-Build or Post-Build?

### Answer to highlight in your presentation:
"ArchUnit runs **DURING THE TEST PHASE (`mvn test`)** — which is **Post-Compilation** of bytecode, but **Pre-Packaging (`mvn package`) and Pre-Deployment (`mvn deploy`)**."

```text
 1. Compile Code        2. Execute ArchUnit Rules        3. Package Artifact       4. Deploy App
┌──────────────────┐    ┌───────────────────────────┐    ┌──────────────────┐    ┌─────────────────┐
│  mvn compile     │───►│  mvn test                 │───►│  mvn package     │───►│  mvn deploy     │
│  (Generates      │    │  (ArchUnit analyzes       │    │  (Generates      │    │  (Pushes to     │
│   .class files)  │    │   compiled bytecodes)     │    │   JAR / WAR)     │    │   Staging/Prod) │
└──────────────────┘    └───────────────────────────┘    └──────────────────┘    └─────────────────┘
                                   │
                           ❌ VIOLATION FOUND
                                   │
                                   ▼
                         🛑 BUILD FAILS IMMEDIATELY!
                         (Package & Deployment are BLOCKED)
```

1. **Why Post-Compilation?**: ArchUnit analyzes compiled `.class` files via Java bytecode inspection. Source code must compile first (`mvn test-compile`).
2. **Why a Pre-Packaging Build Gate?**: `mvn test` executes **BEFORE** JAR packaging (`mvn package`) and Docker/Cloud deployment. If ArchUnit finds a layer violation, Maven **FAILS THE BUILD IMMEDIATELY**. No broken artifact is built, and no bad code reaches production.
3. **Git Pre-Commit Hook**: ArchUnit can also run locally as a Git `pre-commit` hook before `git push`.

---

## 💡 Part 1: Advantages of DDD + Hexagonal Architecture over Traditional Java

When starting the demo, explain why traditional 3-Tier Layered Java architecture degrades over time and how DDD + Hexagonal solves it:

| Architectural Metric | Traditional 3-Tier Layered Java | DDD + Hexagonal Architecture |
|---|---|---|
| **Center of Gravity** | Database & JPA Entities (`@Entity` classes passed through Controller → Service → Repository) | **Domain Core** (Pure Java business logic, isolated from DB & Web) |
| **Dependency Direction** | Top-Down: Controller → Service → Repository → Database | **Inward**: Infrastructure → Application → Domain (Inner Hexagon) |
| **Framework Coupling** | High: `@Service`, `@Entity`, `@Autowired` contaminate business rules | **Zero**: Domain layer uses pure JDK 21 Java Records & Objects |
| **Persistence Flexibility** | Hard: Swapping SQL for MongoDB requires refactoring services | **Plug & Play**: Swap `SatelliteJpaAdapter` for `SatelliteMongoAdapter` without touching domain |
| **Test Execution Speed** | Slow: Requires booting Spring Context or heavy Mockito mocking | **Instant**: Domain unit tests execute in **0.01s** in pure Java |
| **Architecture Safety** | Manual Code Reviews (Prone to human error and erosion) | **Automated CI/CD Gates**: ArchUnit breaks the build on rule violations |

---

## 🚀 Part 2: Step-by-Step Live Presentation Script

### Phase 1: Demonstrate Pure Domain-Driven Design (DDD)

#### 1. Show Domain Purity (`com.satellite.domain.model`)
Open `src/main/java/com/satellite/domain/model/Satellite.java` and highlight:
- *"Notice there are **zero imports from Spring, JPA, Hibernate, or Jackson**. This is pure Java 21."*
- *"The domain is the center of the application — it doesn't know or care whether it runs as a Spring Boot REST API, a CLI script, or a serverless function."*

#### 2. Show Value Objects & Invariant Rules
Open `Orbit.java` and `Telemetry.java`:
- *"Value Objects are immutable JDK records with self-validating rules."*
- "`Orbit.java` enforces LEO (< 2,000 km) and GEO (~35,786 km) orbital constraints."
- "`Telemetry.java` encapsulates anomaly detection logic (battery < 15% or temperature > 80°C)."

#### 3. Show Aggregate Root & State Machine
In `Satellite.java`:
- Point out the status state machine transitions: `REGISTERED` → `ACTIVE` → `ANOMALY` → `DECOMMISSIONED`.
- *"State transitions are guarded inside the aggregate root. Outer services cannot manipulate internal state directly."*

#### 4. Show Domain Events
Open `SatelliteLaunchedEvent.java` and `AnomalyDetectedEvent.java`:
- *"When a satellite launches or triggers an anomaly, the aggregate records an immutable Domain Event."*

---

### Phase 2: Demonstrate Hexagonal Architecture (Ports & Adapters)

#### 1. Show Driving (Inbound) Ports
Open `src/main/java/com/satellite/domain/port/in/LaunchSatelliteUseCase.java`:
- *"Driving ports are Java interfaces defined by the domain representing business use cases."*

#### 2. Show Driving (Inbound) Adapters
Open `src/main/java/com/satellite/infrastructure/adapter/in/rest/SatelliteController.java`:
- *"The REST Controller is an entry adapter converting HTTP JSON requests into use case calls."*
- **Highlight Rule**: *"Notice `SatelliteController` depends ONLY on `LaunchSatelliteUseCase` (the port interface), NOT on concrete service classes or database entities."*

#### 3. Show Driven (Outbound) Ports
Open `src/main/java/com/satellite/domain/port/out/SatelliteRepository.java`:
- *"The domain defines the `SatelliteRepository` interface using domain objects (`Satellite`, `SatelliteId`), completely independent of SQL or ORM."*

#### 4. Show Multi-Database Adapter Swapping (Superpower Demo)
Open `SatelliteJpaAdapter.java` and `SatelliteMongoAdapter.java`:
- *"Here we have two database adapters implementing the same `SatelliteRepository` port."*
- *"We can switch from H2/Postgres (`@Profile("jpa")`) to MongoDB (`@Profile("mongo")`) via Spring configuration without altering a single line of domain code!"*

---

### Phase 3: Working Application Demo Commands (cURL Scripts)

#### 1. Launch Spring Boot Server
```bash
mvn spring-boot:run
```
Application starts on `http://localhost:8080`.

#### 2. Launch Satellite (`POST /api/v1/satellites`)
```bash
curl -X POST http://localhost:8080/api/v1/satellites \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Hubble-2",
    "owner": "NASA",
    "altitudeKm": 540.0,
    "inclinationDegrees": 28.5,
    "orbitType": "LEO"
  }'
```

#### 3. Query Satellite Details (`GET /api/v1/satellites/{id}`)
```bash
curl -X GET http://localhost:8080/api/v1/satellites/<SATELLITE_UUID>
```

#### 4. Send Normal Telemetry Update (`PUT /api/v1/satellites/{id}/telemetry`)
```bash
curl -X PUT http://localhost:8080/api/v1/satellites/<SATELLITE_UUID>/telemetry \
  -H "Content-Type: application/json" \
  -d '{
    "batteryPercentage": 92.5,
    "signalStrengthDbm": -68.0,
    "temperatureCelsius": 21.5
  }'
```

#### 5. Trigger Anomaly State Transition & Event (`PUT /api/v1/satellites/{id}/telemetry`)
Send low battery (< 15%) to trigger `ANOMALY` state transition:
```bash
curl -X PUT http://localhost:8080/api/v1/satellites/<SATELLITE_UUID>/telemetry \
  -H "Content-Type: application/json" \
  -d '{
    "batteryPercentage": 10.0,
    "signalStrengthDbm": -70.0,
    "temperatureCelsius": 20.0
  }'
```

#### 6. List All Satellites (`GET /api/v1/satellites`)
```bash
curl -X GET http://localhost:8080/api/v1/satellites
```

---

### Phase 4: Live ArchUnit Layer Conflict & Build Gate Demo

Demonstrate how ArchUnit prevents architectural erosion when a developer attempts a **layer boundary conflict** (e.g., REST Controller bypassing ports to access application services directly).

#### Step 1: Run Successful Baseline Test
Run Maven tests in your terminal:
```bash
mvn clean test
```
- **Result**: All 37 unit, integration, and ArchUnit architecture tests pass cleanly.

#### Step 2: Introduce a Layer Conflict Violation Live
Open `src/main/java/com/satellite/infrastructure/adapter/in/rest/SatelliteController.java`.

Simulate a developer bypassing the Driving Port contract by injecting `LaunchSatelliteService` directly into the controller:

```java
// ❌ ARCHITECTURE VIOLATION: Importing concrete Application Service inside REST Controller
import com.satellite.application.service.LaunchSatelliteService;

@RestController
@RequestMapping("/api/v1/satellites")
public class SatelliteController {

    // ❌ ARCHITECTURE VIOLATION: Depending directly on application service instead of inbound port
    private final LaunchSatelliteService launchSatelliteService;
    
    // ...
}
```

#### Step 3: Run `mvn test` to Trigger ArchUnit Build Gate
In your terminal, execute:
```bash
mvn test
```

#### Step 4: Show the ArchUnit Violation Failure Report
Maven build **FAILS IMMEDIATELY** with this exact ArchUnit report:

```text
[ERROR] Failures: 
[ERROR]   HexagonalArchitectureTest.controllersMustDependOnPortsNotServices:117 
Architecture Violation [Priority: MEDIUM] - Rule 'no classes residing in com.satellite.infrastructure.adapter.in.rest.. and having simple name ending with 'Controller' should depend on classes residing in com.satellite.application..' was violated (1 times):
Class <com.satellite.infrastructure.adapter.in.rest.SatelliteController> has field <launchSatelliteService> of type <com.satellite.application.service.LaunchSatelliteService> in (SatelliteController.java:28)

[INFO] BUILD FAILURE
```

#### Step 5: Conclude the Presentation
Highlight the key takeaway:
- *"ArchUnit acts as an automated architectural firewall in CI/CD."*
- *"Even if a developer accidentally bypasses ports or leaks database annotations into the domain, ArchUnit stops the code from ever reaching main or production."*
