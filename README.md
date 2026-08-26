# 🛰️ Satellite Management System — DDD + Hexagonal Architecture + ArchUnit

A production-ready Spring Boot 3 reference project demonstrating **Domain-Driven Design (DDD)**, **Hexagonal Architecture (Ports & Adapters)**, **Herberto Graça's Explicit Architecture**, and **ArchUnit Automated Architecture Enforcement**, using a **Satellite Management System** as the domain context.

[![Java 21](https://img.shields.io/badge/Java-21-orange.svg)](https://openjdk.org/projects/jdk/21/)
[![Spring Boot 3.3.4](https://img.shields.io/badge/Spring%20Boot-3.3.4-green.svg)](https://spring.io/projects/spring-boot)
[![ArchUnit 1.3.0](https://img.shields.io/badge/ArchUnit-1.3.0-blue.svg)](https://www.archunit.org/)
[![Build Status](https://img.shields.io/badge/Build-Passing-brightgreen.svg)]()

---

## 📌 Executive Summary Table: What, Why, Components, Implementation, & Challenges

| Aspect | 1. Domain-Driven Design (DDD) | 2. Hexagonal Architecture (Ports & Adapters) | 3. ArchUnit Architecture Enforcement |
|---|---|---|---|
| **What is it?** | A software design methodology focused on modeling complex business domains around core domain logic. | An architectural pattern isolating core logic inside a Hexagon using Inbound/Outbound Ports & Adapters. | A Java architecture testing framework that checks compiled bytecodes via fluent JUnit 5 tests. |
| **Why do we need it?** | Prevents Anemic Domain Models; bridges business stakeholders and developers using a Ubiquitous Language. | Decouples business rules from databases, web frameworks, and UI; enables instant unit testing (0.01s). | Automates architecture governance in CI/CD; prevents monolithic degradation and layer boundary leaks. |
| **Project Components** | `Satellite.java` (Aggregate Root), `Orbit.java` & `Telemetry.java` (Value Objects), `SatelliteLaunchedEvent`. | Driving Adapters (`SatelliteController`), Driving Ports (`LaunchSatelliteUseCase`), Driven Ports (`SatelliteRepository`), Driven Adapters (`SatelliteJpaAdapter`, `SatelliteMongoAdapter`). | 11 Build Gates in `HexagonalArchitectureTest.java` enforcing layer hierarchy and class naming rules. |
| **How to Implement?** | Write pure Java records/entities in `com.satellite.domain`; keep zero Spring/JPA annotations in model. | Structure inward packages (`infrastructure` → `application` → `domain`); wire dependencies in `BeanConfig.java`. | Add `archunit-junit5`, annotate test class with `@AnalyzeClasses`, and write `@ArchTest` rules. |
| **Challenges & Mitigations** | Initial learning curve & domain boundary discovery -> Solved with Ubiquitous Language glossary. | Mapping overhead (Domain <-> DTO <-> Entity) -> Solved with dedicated mappers (`SatellitePersistenceMapper`). | Retrofitting legacy codebases -> Solved with `FreezingArchRule` to freeze existing technical debt. |

---

## ❓ Does ArchUnit Run as Pre-Build or Post-Build?

ArchUnit runs **DURING THE TEST PHASE (`mvn test`)** — which is **Post-Compilation** of bytecode, but **Pre-Packaging (`mvn package`) & Pre-Deployment (`mvn deploy`)**:

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

---

## 🖼️ Architecture Diagram Visualizations

The project includes **5 high-definition graphic diagrams** modeled directly after classical architectural specifications ([Herberto Graça Explicit Architecture](https://github.com/mehdihadeli/awesome-software-architecture/blob/main/docs/hexagonal-architecture.md) & WATA Factory Hexagonal standards):

1. **Herberto Graça Explicit Architecture (`diagram_explicit_architecture.png`)**: Synthesizes DDD, Hexagonal, Clean, and Onion Architecture into explicit application boundaries. Maps Primary Adapters (REST Controllers / CLI), Application Core (Use Cases & Commands), Domain Layer (Aggregates & Value Objects), and Secondary Adapters (JPA / Mongo / Messaging).
2. **Primary & Secondary Adapters (`diagram_hexagonal_primary_secondary.png`)**: Visualizes Primary/Driving Adapters on the left (`SatelliteController.java`), Application Use Cases in the middle (`LaunchSatelliteService.java`), Domain Core in the center (`Satellite.java`), and Secondary/Driven Adapters on the right (`SatelliteJpaAdapter.java` -> SQL DB, `SatelliteMongoAdapter.java` -> NoSQL DB).
3. **Concentric Layered Onion Architecture (`diagram_onion_concentric.png`)**: Concentric ring view enforcing the inward dependency rule.
4. **DDD Ubiquitous Language & Subdomains (`diagram_ddd_ubiquitous_language.png`)**: Distills Problem Domain, Ubiquitous Language, Core Subdomain, Supporting Subdomain, and Generic Subdomain.
5. **Multi-Database Adapter Swapping Demo (`diagram_adapter_swapping.png`)**: Demonstrates how `SatelliteRepository` outbound port seamlessly switches between Relational DB (`SatelliteJpaAdapter`) and NoSQL Document DB (`SatelliteMongoAdapter`) with **zero changes to Domain Core**.

---

## 🔌 Hexagonal Superpower: Multi-Database Adapter Swapping Demo

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

---

## 🧪 Validation & Testing Guide

```bash
# 1. Execute Automated Test Suite & ArchUnit Enforcement (37/37 passing)
mvn clean test

# 2. Run Spring Boot Application Locally
mvn spring-boot:run

# 3. Test Satellite Launch (POST API)
curl -X POST http://localhost:8080/api/v1/satellites \
  -H "Content-Type: application/json" \
  -d '{"name":"Hubble-2","owner":"NASA","altitudeKm":540.0,"inclinationDegrees":28.5,"orbitType":"LEO"}'
```
