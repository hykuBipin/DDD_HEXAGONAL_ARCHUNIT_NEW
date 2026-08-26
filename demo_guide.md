# 🎬 Step-by-Step Presentation & Live Demo Guide

This guide provides a structured, phase-by-phase script for presenting **Domain-Driven Design (DDD)**, **Hexagonal Architecture (Ports & Adapters)**, and **ArchUnit Architecture Enforcement** using the Satellite Management System codebase.

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
Open `src/main/java/com/satellite/domain/model/Satellite.java` and explain:
- "Notice there are **zero imports from Spring, JPA, Hibernate, or Jackson**. This is pure Java 21."
- "The domain is the center of the universe — it does not know or care if the app runs as a Spring Boot REST API, a CLI tool, or a serverless Lambda."

#### 2. Show Value Objects & Invariant Rules
Open `Orbit.java` and `Telemetry.java`:
- "Value Objects are immutable JDK records with built-in self-validation."
- "`Orbit.java` enforces LEO (< 2,000 km) and GEO (~35,786 km) altitude constraints upon creation."
- "`Telemetry.java` contains domain logic to evaluate anomaly thresholds (battery < 15% or temp > 80°C)."

#### 3. Show Aggregate Root & State Machine
In `Satellite.java`:
- Point out the status transitions: `REGISTERED` → `ACTIVE` → `ANOMALY` → `DECOMMISSIONED`.
- "State transitions are guarded inside the aggregate root. Outer services cannot manipulate internal state directly."

#### 4. Show Domain Events
Open `SatelliteLaunchedEvent.java` and `AnomalyDetectedEvent.java`:
- "When a satellite launches or experiences an anomaly, the aggregate records a Domain Event internally."

---

### Phase 2: Demonstrate Hexagonal Architecture (Ports & Adapters)

#### 1. Show Driving (Inbound) Ports
Open `src/main/java/com/satellite/domain/port/in/LaunchSatelliteUseCase.java`:
- "Driving ports are Java interfaces defined by the domain representing business use cases."

#### 2. Show Driving (Inbound) Adapters
Open `src/main/java/com/satellite/infrastructure/adapter/in/rest/SatelliteController.java`:
- "The REST Controller is an entry adapter. It converts HTTP JSON requests into use case calls."
- **Highlight Rule**: "Notice `SatelliteController` depends ONLY on `LaunchSatelliteUseCase` (the port interface), NOT on concrete service classes or database entities."

#### 3. Show Driven (Outbound) Ports
Open `src/main/java/com/satellite/domain/port/out/SatelliteRepository.java`:
- "The domain defines the `SatelliteRepository` interface using domain objects (`Satellite`, `SatelliteId`), completely independent of SQL or ORM."

#### 4. Show Multi-Database Adapter Swapping (Superpower Demo)
Open `SatelliteJpaAdapter.java` and `SatelliteMongoAdapter.java`:
- "Here we have two database adapters implementing the same `SatelliteRepository` port."
- "We can switch from H2/Postgres (`@Profile("jpa")`) to MongoDB (`@Profile("mongo")`) via Spring configuration without altering a single line of domain code!"

---

### Phase 3: Live ArchUnit Layer Conflict & Build Gate Demo

Demonstrate how ArchUnit prevents architectural erosion when a developer attempts a **layer boundary conflict** (e.g. REST Controller directly accessing application services or JPA entities).

#### Step 1: Run Successful Baseline Test
Run Maven tests in your terminal:
```bash
mvn clean test
```
- **Result**: All 37 unit, integration, and ArchUnit architecture tests pass cleanly.

#### Step 2: Introduce a Layer Conflict Violation
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

#### Step 5: Explain the Power of ArchUnit Build Gates
Conclude the demo by highlighting:
- "ArchUnit acts as an automated architectural firewall in CI/CD."
- "Even if a developer accidentally bypasses ports or leaks database annotations into the domain, ArchUnit stops the code from ever reaching main or production."
