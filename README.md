# 🛰️ Satellite Management System — DDD + Hexagonal Architecture + ArchUnit

A production-ready Spring Boot 3 reference project demonstrating **Domain-Driven Design (DDD)**, **Hexagonal Architecture (Ports & Adapters)**, and **ArchUnit Automated Architecture Enforcement**, using a **Satellite Management System** as the domain context.

[![Java 21](https://img.shields.io/badge/Java-21-orange.svg)](https://openjdk.org/projects/jdk/21/)
[![Spring Boot 3.3.4](https://img.shields.io/badge/Spring%20Boot-3.3.4-green.svg)](https://spring.io/projects/spring-boot)
[![ArchUnit 1.3.0](https://img.shields.io/badge/ArchUnit-1.3.0-blue.svg)](https://www.archunit.org/)
[![Build Status](https://img.shields.io/badge/Build-Passing-brightgreen.svg)]()

---

## 🏛️ Traditional Java vs. DDD + Hexagonal Architecture

### Traditional 3-Tier Layered Architecture (The Problem)
In traditional Spring Boot applications, architecture is structured around database entities:
```
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
│                        infrastructure.adapter.out (JPA, Messaging)     │
└────────────────────────────────────────────────────────────────────────┘
```

1. **Inside the Hexagon (`com.satellite.domain`)**: Pure Java. Contains Aggregate Roots, Value Objects, Domain Events, and Port Interfaces. Zero framework dependencies.
2. **Ports (`domain.port.in` & `domain.port.out`)**: Contracts defining how the outside world talks to the domain (Inbound) and how the domain communicates with external systems (Outbound).
3. **Outside the Hexagon (`com.satellite.infrastructure`)**: Technical details (Spring MVC, Spring Data JPA, Kafka adapters, Bean configurations).

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
│   │       ├── persistence/                     (Driven Adapter — JPA Persistence)
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
