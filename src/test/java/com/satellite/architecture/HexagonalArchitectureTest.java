package com.satellite.architecture;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.*;
import static com.tngtech.archunit.library.Architectures.layeredArchitecture;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

/**
 * ═══════════════════════════════════════════════════════════════════════════
 *  HEXAGONAL ARCHITECTURE ENFORCEMENT WITH ARCHUNIT
 * ═══════════════════════════════════════════════════════════════════════════
 *
 * These tests are NOT optional documentation — they are MANDATORY build gates.
 * If any rule is violated, the build FAILS. This prevents architectural drift.
 *
 * Architecture being enforced:
 *
 *   ┌────────────────────────────────────────────────────────────┐
 *   │  adapter.in  (REST, Messaging)                             │
 *   │      │                                                     │
 *   │      ▼                                                     │
 *   │  domain.port.in  ◄──── application.service                │
 *   │                              │                             │
 *   │                              ▼                             │
 *   │                        domain.port.out                     │
 *   │                              │                             │
 *   │                              ▼                             │
 *   │                        adapter.out                         │
 *   └────────────────────────────────────────────────────────────┘
 *
 *   domain  ──►  NO dependency on anything else (inner hexagon)
 */
@AnalyzeClasses(
        packages = "com.satellite",
        importOptions = ImportOption.DoNotIncludeTests.class
)
public class HexagonalArchitectureTest {

    private static final String BASE = "com.satellite";

    // ── Rule 1: Domain layer purity ───────────────────────────────────────────
    /**
     * The domain package must NEVER depend on adapter, application, or config.
     * It may not use Spring, JPA, Jackson, or any framework annotations.
     */
    @ArchTest
    static final ArchRule domainMustNotDependOnAdapters =
            noClasses()
                    .that().resideInAPackage(BASE + ".domain..")
                    .should().dependOnClassesThat()
                    .resideInAnyPackage(
                            BASE + ".adapter..",
                            BASE + ".application..",
                            BASE + ".config.."
                    )
                    .because("The domain is the inner hexagon — it must depend on nothing outside itself.");

    // ── Rule 2: No Spring annotations in domain model ─────────────────────────
    @ArchTest
    static final ArchRule domainModelMustNotUseSpring =
            noClasses()
                    .that().resideInAPackage(BASE + ".domain.model..")
                    .should().beAnnotatedWith("org.springframework.stereotype.Component")
                    .orShould().beAnnotatedWith("org.springframework.stereotype.Service")
                    .orShould().beAnnotatedWith("org.springframework.stereotype.Repository")
                    .because("Domain model classes must be plain Java objects without Spring coupling.");

    // ── Rule 3: No JPA annotations in domain model ────────────────────────────
    @ArchTest
    static final ArchRule domainModelMustNotUseJpa =
            noClasses()
                    .that().resideInAPackage(BASE + ".domain.model..")
                    .should().beAnnotatedWith("jakarta.persistence.Entity")
                    .orShould().beAnnotatedWith("jakarta.persistence.Table")
                    .because("JPA is an infrastructure concern. Domain model must stay annotation-free.");

    // ── Rule 4: Application services must depend on domain ports ──────────────
    @ArchTest
    static final ArchRule applicationServicesMustDependOnInboundPorts =
            classes()
                    .that().resideInAPackage(BASE + ".application.service..")
                    .and().haveSimpleNameEndingWith("Service")
                    .should().dependOnClassesThat()
                    .resideInAPackage(BASE + ".domain.port.in..")
                    .because("Application services must implement inbound port interfaces.");

    // ── Rule 5: Application layer must not depend on adapters ─────────────────
    @ArchTest
    static final ArchRule applicationMustNotDependOnAdapters =
            noClasses()
                    .that().resideInAPackage(BASE + ".application..")
                    .should().dependOnClassesThat()
                    .resideInAPackage(BASE + ".adapter..")
                    .because("Application services orchestrate domain logic only via ports.");

    // ── Rule 6: Adapters must not depend on each other ────────────────────────
    @ArchTest
    static final ArchRule inboundAdaptersMustNotDependOnOutboundAdapters =
            noClasses()
                    .that().resideInAPackage(BASE + ".adapter.in..")
                    .should().dependOnClassesThat()
                    .resideInAPackage(BASE + ".adapter.out..")
                    .because("Inbound adapters (REST/Messaging) must not know about outbound adapters (DB/MQ).");

    // ── Rule 7: REST controllers must only depend on domain ports (not services) ──
    @ArchTest
    static final ArchRule controllersMustDependOnPortsNotServices =
            noClasses()
                    .that().resideInAPackage(BASE + ".adapter.in.rest..")
                    .and().haveSimpleNameEndingWith("Controller")
                    .should().dependOnClassesThat()
                    .resideInAPackage(BASE + ".application..")
                    .because("REST controllers must talk to ports, not directly to application services.");

    // ── Rule 8: Naming conventions ────────────────────────────────────────────
    @ArchTest
    static final ArchRule servicesShouldBeNamedCorrectly =
            classes()
                    .that().resideInAPackage(BASE + ".application.service..")
                    .should().haveSimpleNameEndingWith("Service")
                    .because("Application services must follow the naming convention *Service.");

    @ArchTest
    static final ArchRule adaptersShouldBeNamedCorrectly =
            classes()
                    .that().resideInAPackage(BASE + ".adapter.out.persistence")
                    .and().areNotInterfaces()
                    .and().doNotHaveSimpleName("SatellitePersistenceMapper")
                    .should().haveSimpleNameEndingWith("Adapter")
                    .because("Outbound persistence adapters must follow the naming convention *Adapter.");

    @ArchTest
    static final ArchRule controllersShouldBeNamedCorrectly =
            classes()
                    .that().resideInAPackage(BASE + ".adapter.in.rest")
                    .and().areNotInterfaces()
                    .and().areNotRecords()
                    .and().areTopLevelClasses()      // excludes inner records like ErrorResponse
                    .should().haveSimpleNameEndingWith("Controller")
                    .because("REST adapter classes must follow the naming convention *Controller.");

    // ── Rule 9: Ports must reside in domain ───────────────────────────────────
    @ArchTest
    static final ArchRule inboundPortsMustBeInDomain =
            classes()
                    .that().resideInAPackage(BASE + ".domain.port.in..")
                    .should().resideInAPackage(BASE + ".domain..")
                    .because("Inbound ports are part of the domain hexagon.");

    @ArchTest
    static final ArchRule outboundPortsMustBeInDomain =
            classes()
                    .that().resideInAPackage(BASE + ".domain.port.out..")
                    .should().resideInAPackage(BASE + ".domain..")
                    .because("Outbound ports are part of the domain hexagon.");

    // ── Rule 10: No circular dependencies between slices ─────────────────────
    @ArchTest
    static final ArchRule noCyclicDependencies =
            slices()
                    .matching(BASE + ".(*)..") // top-level packages: domain, application, adapter, config
                    .should().beFreeOfCycles()
                    .because("Cyclic dependencies between layers indicate broken architecture.");

    // ── Rule 11: Layered Architecture (coarse-grained) ────────────────────────
    @ArchTest
    static final ArchRule layeredArchitectureIsRespected =
            layeredArchitecture()
                    .consideringOnlyDependenciesInAnyPackage(BASE + "..")
                    .layer("Domain")      .definedBy(BASE + ".domain..")
                    .layer("Application") .definedBy(BASE + ".application..")
                    .layer("Adapter")     .definedBy(BASE + ".adapter..")
                    .layer("Config")      .definedBy(BASE + ".config..")

                    .whereLayer("Domain")      .mayNotAccessAnyLayer()
                    .whereLayer("Application") .mayOnlyAccessLayers("Domain")
                    .whereLayer("Adapter")     .mayOnlyAccessLayers("Domain", "Application")
                    .whereLayer("Config")      .mayOnlyAccessLayers("Domain", "Application", "Adapter")

                    .because("Layered architecture must be respected: " +
                             "Config → Adapter → Application → Domain (inner hexagon).");
}
