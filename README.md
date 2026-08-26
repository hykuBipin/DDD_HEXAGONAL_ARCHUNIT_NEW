# 🛰️ Satellite Management System — DDD + Hexagonal Architecture + ArchUnit

A production-ready Spring Boot 3 reference project demonstrating **Domain-Driven Design (DDD)**, **Hexagonal Architecture (Ports & Adapters)**, **Herberto Graça's Explicit Architecture**, and **ArchUnit Automated Architecture Enforcement**, using a **Satellite Management System** as the domain context.

[![Java 21](https://img.shields.io/badge/Java-21-orange.svg)](https://openjdk.org/projects/jdk/21/)
[![Spring Boot 3.3.4](https://img.shields.io/badge/Spring%20Boot-3.3.4-green.svg)](https://spring.io/projects/spring-boot)
[![ArchUnit 1.3.0](https://img.shields.io/badge/ArchUnit-1.3.0-blue.svg)](https://www.archunit.org/)
[![Build Status](https://img.shields.io/badge/Build-Passing-brightgreen.svg)]()

---

## 🖼️ Architecture Diagram Visualizations

The project includes **5 high-definition graphic diagrams** modeled directly after classical architectural specifications ([Herberto Graça Explicit Architecture](https://github.com/mehdihadeli/awesome-software-architecture/blob/main/docs/hexagonal-architecture.md) & WATA Factory Hexagonal standards), populated with our exact Satellite codebase Java classes:

1. **Herberto Graça Explicit Architecture (`diagram_explicit_architecture.png`)**: Synthesizes DDD, Hexagonal, Clean, and Onion Architecture into explicit application boundaries. Maps Primary Adapters (REST Controllers / CLI), Application Core (Use Cases & Commands), Domain Layer (Aggregates & Value Objects), and Secondary Adapters (JPA / Mongo / Messaging).
2. **Primary & Secondary Adapters (`diagram_hexagonal_primary_secondary.png`)**: Modeled after WATA Factory / Herberto Graça Hexagonal specs. Visualizes Primary/Driving Adapters on the left (`SatelliteController.java`, `TelemetryConsumer.java`), Application Use Cases in the middle (`LaunchSatelliteService.java`), Domain Core in the center (`Satellite.java`, `Orbit.java`), and Secondary/Driven Adapters on the right (`SatelliteJpaAdapter.java` -> SQL DB, `SatelliteMongoAdapter.java` -> NoSQL DB, `SatelliteEventPublisherAdapter.java` -> Spring Events).
3. **Concentric Layered Onion Architecture (`diagram_onion_concentric.png`)**: Concentric ring view enforcing the inward dependency rule — outer Infrastructure & Adapters depend inward on Application Services, Domain Ports, and the untouched Core Domain Model.
4. **DDD Ubiquitous Language & Subdomains (`diagram_ddd_ubiquitous_language.png`)**: Distills the Satellite Problem Domain, Ubiquitous Language ("Orbital Inclination", "LEO/GEO Orbit", "Telemetry Anomaly Threshold"), Core Subdomain (`Satellite` Aggregate Root & State Machine), Supporting Subdomain (`Telemetry` Anomaly Detector), and Generic Subdomain (Persistence & Security).
5. **Multi-Database Adapter Swapping Demo (`diagram_adapter_swapping.png`)**: Demonstrates how `SatelliteRepository` outbound port seamlessly switches between Relational DB (`SatelliteJpaAdapter`) and NoSQL Document DB (`SatelliteMongoAdapter`) with **zero changes to Domain Core**.

---

## 🏛️ Traditional Java vs. DDD + Hexagonal Architecture

### Traditional 3-Tier Layered Architecture (The Problem)
In traditional Spring Boot applications, architecture is structured around database entities:
```text
Controller ──► Service (@Service) ──► Repository (@Repository) ──► Database (@Entity)
```
- **Database-Centric**: JPA `@Entity` classes leak into controllers and services.
- **Framework Coupling**: `@Autowired`, `@Service`, and `@Entity` contaminate business logic.
- **Anemic Domain Model**: Data objects contain getters/setters while business logic is scattered across heavy service classes.
- **Hard to Test**: Testing business rules requires booting Spring Context or mocking database repositories.
- **Architectural Decay**: Over time, developers bypass layers, creating circular dependencies and spaghetti code.

---

### DDD + Hexagonal Architecture (The Solution)
Hexagonal Architecture places the **Domain Core** at the center, isolated from all external concerns (UI, Databases, Frameworks, Messaging):

```text
┌────────────────────────────────────────────────────────────────────────┐
│  infrastructure.adapter.in  (REST Controller, Kafka Consumer)          │
│      │                                                                 │
│      ▼                                                                 │
│  domain.port.in  ◄──── application.service (Orchestrator)             │
│                              │                                         │
│                              ▼                                         │
│                        domain.port.out                                 │
│                              │                                         │
│                              ▼                                         │
│                        infrastructure.adapter.out (JPA, MongoDB)       │
└────────────────────────────────────────────────────────────────────────┘
```

---

## 🔌 Hexagonal Superpower: Multi-Database Adapter Swapping Demo

One of the greatest benefits of Hexagonal Architecture is the ability to **swap or support multiple databases without touching a single line of domain or application service code**.

### Outbound Port (Domain Contract)
Located in `com.satellite.domain.port.out.SatelliteRepository`:
```java
public interface SatelliteRepository {
    Satellite save(Satellite satellite);
    Optional<Satellite> findById(SatelliteId id);
    List<Satellite> findAll();
    boolean existsById(SatelliteId id);
}
```

### Driven Adapter 1: Relational SQL DB (JPA / H2 / PostgreSQL)
Located in `com.satellite.infrastructure.adapter.out.persistence.SatelliteJpaAdapter`:
```java
@Component
@Profile("jpa") // Active for SQL databases
public class SatelliteJpaAdapter implements SatelliteRepository {

    private final SatelliteJpaRepository jpaRepository;
    private final SatellitePersistenceMapper mapper;

    @Override
    public Satellite save(Satellite satellite) {
        SatelliteJpaEntity entity = mapper.toJpaEntity(satellite);
        SatelliteJpaEntity saved  = jpaRepository.save(entity);
        return mapper.toDomainEntity(saved);
    }
}
```

### Driven Adapter 2: NoSQL Document DB (MongoDB)
Located in `com.satellite.infrastructure.adapter.out.persistence.SatelliteMongoAdapter`:
```java
@Component
@Profile("mongo") // Active for NoSQL Document DBs
public class SatelliteMongoAdapter implements SatelliteRepository {

    private final MongoTemplate mongoTemplate;
    private final SatelliteMongoMapper mapper;

    @Override
    public Satellite save(Satellite satellite) {
        SatelliteDocument doc = mapper.toDocument(satellite);
        SatelliteDocument saved = mongoTemplate.save(doc);
        return mapper.toDomainEntity(saved);
    }
}
```

> 💡 **Key Takeaway**: Switching from PostgreSQL to MongoDB or Redis is purely a configuration swap (`@Profile` or Spring Bean config). The Domain aggregate `Satellite.java` and Use Cases remain **100% untouched and unaware** of where data is stored!

---

## 📐 Package Structure (JonathanM2ndoza Standard)

```text
com.satellite/
├── domain/                                      (Inner Hexagon — Pure Java)
│   ├── model/
│   │   ├── Satellite.java                       (Aggregate Root & State Machine)
│   │   ├── Orbit.java                           (Value Object — Altitude & Inclination Rules)
│   │   ├── Telemetry.java                       (Value Object — Anomaly Detection Rules)
│   │   ├── SatelliteId.java                     (Strongly-typed UUID Record)
│   │   └── SatelliteStatus.java                 (Lifecycle Enum)
│   ├── event/
│   │   ├── SatelliteLaunchedEvent.java          (Domain Event)
│   │   └── AnomalyDetectedEvent.java            (Domain Event)
│   ├── exception/
│   │   └── SatelliteDomainException.java        (Domain Exception)
│   └── port/
│       ├── in/                                  (Driving Ports — Use Case Contracts)
│       │   ├── LaunchSatelliteUseCase.java
│       │   ├── UpdateTelemetryUseCase.java
│       │   └── GetSatelliteUseCase.java
│       └── out/                                 (Driven Ports — Infrastructure Contracts)
│           ├── SatelliteRepository.java
│           └── SatelliteEventPublisher.java
│
├── application/                                 (Use Case Orchestration — Pure Java)
│   └── service/
│       ├── LaunchSatelliteService.java
│       ├── UpdateTelemetryService.java
│       └── GetSatelliteService.java
│
├── infrastructure/                              (Adapters & Technical Concerns)
│   ├── adapter/
│   │   ├── in/rest/                             (Driving Adapter — REST Controller)
│   │   │   ├── SatelliteController.java
│   │   │   ├── dto/                             (HTTP Requests & Responses)
│   │   │   └── mapper/                          (Domain <-> DTO Mapper)
│   │   └── out/
│   │       ├── persistence/                     (Driven Adapters — JPA / MongoDB)
│   │       │   ├── SatelliteJpaAdapter.java
│   │       │   ├── entity/                      (SatelliteJpaEntity & SatelliteJpaRepository)
│   │       │   └── mapper/                      (Domain <-> JPA Mapper)
│   │       └── messaging/                       (Driven Adapter — Spring Event Publisher)
│   │           └── SatelliteEventPublisherAdapter.java
│   └── config/                                  (Spring Composition Root)
│       └── BeanConfig.java
│
└── SatelliteSystemApplication.java
```

---

## 🛡️ ArchUnit Architecture Rules Explained

`HexagonalArchitectureTest.java` contains **11 automated ArchUnit rules** that act as mandatory CI/CD build gates. If any rule is violated, `mvn test` fails immediately.

| Rule # | Rule Name | Description & Rationale |
|---|---|---|
| **Rule 1** | `domainMustNotDependOnAdapters` | `domain` package must NEVER import or depend on `infrastructure` or `application`. Guarantees domain purity. |
| **Rule 2** | `domainModelMustNotUseSpring` | Domain model classes must NOT use `@Component`, `@Service`, or `@Repository`. Business logic remains framework-free. |
| **Rule 3** | `domainModelMustNotUseJpa` | Domain model classes must NOT use `@Entity`, `@Table`, or JPA annotations. Database structure changes won't affect business logic. |
| **Rule 4** | `applicationServicesMustDependOnInboundPorts` | `application.service` classes must implement `domain.port.in` interfaces. |
| **Rule 5** | `applicationMustNotDependOnAdapters` | Application services orchestrate domain logic only via ports and must never depend on concrete adapters or database drivers. |
| **Rule 6** | `inboundAdaptersMustNotDependOnOutboundAdapters` | REST Controllers (`infrastructure.adapter.in`) cannot directly access DB Adapters (`infrastructure.adapter.out`). |
| **Rule 7** | `controllersMustDependOnPortsNotServices` | REST Controllers talk **only** to inbound port interfaces (`LaunchSatelliteUseCase`), never directly to service implementation classes. |
| **Rule 8** | `namingConventions` | Services end in `Service`, persistence adapters end in `Adapter`, and controllers end in `Controller`. |
| **Rule 9** | `portsMustResideInDomain` | Driving (`port.in`) and driven (`port.out`) port interfaces MUST be defined inside the `domain` package. |
| **Rule 10** | `noCyclicDependencies` | Enforces zero circular package dependencies between slices. |
| **Rule 11** | `layeredArchitectureIsRespected` | Strict layered access: `Infrastructure` → `Application` → `Domain` (inner hexagon cannot access outer layers). |

---

## 🧪 Validation & Testing Guide

### 1. Execute Automated Test Suite & ArchUnit Enforcement

Run all unit, integration, and ArchUnit architecture tests:

```bash
mvn clean test
```

**Expected Output**:
```text
[INFO] Running com.satellite.infrastructure.adapter.SatelliteControllerIntegrationTest
[INFO] Tests run: 4, Failures: 0, Errors: 0, Skipped: 0
[INFO] Running com.satellite.application.ApplicationServiceTest
[INFO] Tests run: 5, Failures: 0, Errors: 0, Skipped: 0
[INFO] Running com.satellite.architecture.HexagonalArchitectureTest
[INFO] Tests run: 14, Failures: 0, Errors: 0, Skipped: 0
[INFO] Running com.satellite.domain.SatelliteTest
[INFO] Tests run: 14, Failures: 0, Errors: 0, Skipped: 0
[INFO] 
[INFO] Results:
[INFO] Tests run: 37, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

---

### 2. Run Application Server Locally

Start the Spring Boot application:

```bash
mvn spring-boot:run
```

App starts at `http://localhost:8080`.

---

### 3. End-to-End API Testing with cURL

#### A. Launch a Satellite (`POST /api/v1/satellites`)

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

#### B. Query Satellite Details (`GET /api/v1/satellites/{id}`)

```bash
curl -X GET http://localhost:8080/api/v1/satellites/<SATELLITE_UUID>
```

#### C. Send Normal Telemetry Update (`PUT /api/v1/satellites/{id}/telemetry`)

```bash
curl -X PUT http://localhost:8080/api/v1/satellites/<SATELLITE_UUID>/telemetry \
  -H "Content-Type: application/json" \
  -d '{
    "batteryPercentage": 92.5,
    "signalStrengthDbm": -68.0,
    "temperatureCelsius": 21.5
  }'
```

#### D. Trigger Anomaly & Domain Event (`PUT /api/v1/satellites/{id}/telemetry`)

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

#### E. List All Satellites (`GET /api/v1/satellites`)

```bash
curl -X GET http://localhost:8080/api/v1/satellites
```

---

### 4. Test Architecture Rule Violation (Live ArchUnit Enforcement Demo)

1. Open `src/main/java/com/satellite/domain/model/Satellite.java`.
2. Add a forbidden import:
   ```java
   import org.springframework.stereotype.Component;
   ```
3. Run `mvn test`.
4. **ArchUnit output**:
   ```text
   Architecture Violation [Priority: MEDIUM] - Rule 'no classes residing in domain.model should be annotated with @Component' was violated.
   ```
