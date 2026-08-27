# 🎬 Step-by-Step Presentation & Live Demo Guide

This guide provides a structured, phase-by-phase script for presenting **Domain-Driven Design (DDD)**, **Hexagonal Architecture (Ports & Adapters)**, **Herberto Graça's Explicit Architecture**, and **ArchUnit Architecture Enforcement** using the Satellite Management System codebase.

---

## 🛠️ Surefire HTML Test Report Setup & Troubleshooting

### Why HTML Reports Are Not Automatically Generated During a Test Failure

When a test fails during `mvn test` or `mvn test -Parchitecture-test`:

```text
compile ──► testCompile ──► Surefire (ArchUnit) ──► ❌ 1 Failure ──► BUILD FAILURE (STOP!)
```

Because Maven stops immediately on test failure, downstream goals bound to `verify` or `package` phases **never execute**. However, Maven **ALREADY SAVED** the XML and TXT test failure results at:
- `target/surefire-reports/TEST-com.satellite.architecture.HexagonalArchitectureTest.xml`
- `target/surefire-reports/com.satellite.architecture.HexagonalArchitectureTest.txt`

---

### Production Workflow: Generate HTML Report Even After Test Failures

To generate `target/site/surefire-report.html` **without re-running tests** (even after an ArchUnit failure), follow these two steps:

#### Step 1: Run ArchUnit Tests
```bash
mvn clean test -Parchitecture-test
```
*(Expected: `BUILD FAILURE` with 1 failed test).*

#### Step 2: Read Raw Failure Logs (Terminal)
```bash
cat target/surefire-reports/com.satellite.architecture.HexagonalArchitectureTest.txt
```

#### Step 3: Generate HTML Report from Existing Results (No Re-run)
```bash
mvn surefire-report:report-only
```
> **Note**: Always use `surefire-report:report-only` instead of `surefire-report:report`. `report-only` reads existing XML reports from `target/surefire-reports/` without re-triggering the test lifecycle!

#### Step 4: Verify HTML Report Generation
```bash
find target/site -type f -name "*.html" -print
```
**Output**:
```text
target/site/surefire-report.html
```

---

## 📸 Visual Evidence & Screenshots

### 1. ArchUnit Layer Conflict Failure in Surefire HTML Report
![ArchUnit Layer Conflict Failure](docs/evidence/evidence_surefire_archunit_failure.png)
> **Figure 1**: Surefire HTML Report (`target/site/surefire-report.html`) generated via `mvn surefire-report:report-only` showing the failed rule `controllersMustDependOnPortsNotServices`. ArchUnit detects that `SatelliteController` illegally injected `LaunchSatelliteService` instead of depending solely on the `LaunchSatelliteUseCase` port interface!

---

### 2. Postman API — Launch Satellite (`POST /api/v1/satellites`)
![Postman POST Launch Satellite](docs/evidence/evidence_postman_launch_satellite.png)
> **Figure 2**: Postman client executing `POST http://localhost:8080/api/v1/satellites` returning `HTTP 201 Created` with initialized satellite status `ACTIVE` and generated UUID (`b1a483b4-1589-4b6d-846a-23fc80b7910f`).

---

### 3. Postman API — Normal Telemetry (`PUT /telemetry`)
![Postman PUT Telemetry Normal](docs/evidence/evidence_postman_telemetry_normal.png)
> **Figure 3**: Postman client updating telemetry with healthy battery (92.5%) and temperature (21.5°C) returning `HTTP 200 OK`.

---

### 4. Postman API — Low Battery Anomaly Trigger (`PUT /telemetry`)
![Postman PUT Telemetry Anomaly](docs/evidence/evidence_postman_telemetry_anomaly.png)
> **Figure 4**: Postman client submitting low battery telemetry (10.0%). The domain aggregate `Satellite.java` evaluates the invariant rule, triggers the `ANOMALY` state transition (`isAnomalous: true`), and emits a `AnomalyDetectedEvent`!

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
mvn test -Parchitecture-test
```
*(Result: `BUILD FAILURE` at Surefire stage).*

#### Step 4: Generate HTML Report Post-Failure Without Re-running Tests
```bash
mvn surefire-report:report-only
```

#### Step 5: View Generated HTML Report
Open `target/site/surefire-report.html` in your browser. It renders the full HTML table showing `controllersMustDependOnPortsNotServices` failed with the exact class and field violation details!

#### Step 6: Conclude the Presentation
Highlight the key takeaway:
- *"ArchUnit acts as an automated architectural firewall in CI/CD."*
- *"Even if a developer accidentally bypasses ports or leaks database annotations into the domain, ArchUnit stops the code from ever reaching main or production."*
