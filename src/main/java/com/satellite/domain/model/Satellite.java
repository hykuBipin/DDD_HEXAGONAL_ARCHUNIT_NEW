package com.satellite.domain.model;

import com.satellite.domain.event.AnomalyDetectedEvent;
import com.satellite.domain.event.SatelliteLaunchedEvent;
import com.satellite.domain.exception.SatelliteDomainException;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Satellite Aggregate Root.
 *
 * <p>This is the heart of the domain. All business invariants, state transitions,
 * and domain events are managed here. No JPA, Spring, or JSON annotations allowed —
 * this is pure domain logic.
 *
 * <p>Domain events are collected internally and consumed by the application layer
 * after a use case completes (transactional outbox pattern).
 */
public class Satellite {

    // ── Identity ──────────────────────────────────────────────────────────────
    private final SatelliteId id;
    private final String name;
    private final String owner;
    private final Instant registeredAt;

    // ── Mutable state ─────────────────────────────────────────────────────────
    private Orbit orbit;
    private Telemetry telemetry;
    private SatelliteStatus status;
    private Instant lastUpdatedAt;

    // ── Domain Events (cleared after persistence) ─────────────────────────────
    private final List<Object> domainEvents = new ArrayList<>();

    // ── Constructor (use factory method for creation) ─────────────────────────
    private Satellite(SatelliteId id, String name, String owner, Orbit orbit, Instant registeredAt) {
        this.id           = Objects.requireNonNull(id, "Satellite ID must not be null");
        this.name         = validateName(name);
        this.owner        = Objects.requireNonNull(owner, "Owner must not be null");
        this.orbit        = Objects.requireNonNull(orbit, "Orbit must not be null");
        this.registeredAt = registeredAt;
        this.status       = SatelliteStatus.REGISTERED;
        this.lastUpdatedAt = registeredAt;
    }

    // ── Factory method ────────────────────────────────────────────────────────

    /**
     * Creates and registers a new satellite. Use this instead of 'new'.
     */
    public static Satellite register(String name, String owner, Orbit orbit) {
        return new Satellite(SatelliteId.generate(), name, owner, orbit, Instant.now());
    }

    /**
     * Reconstitutes a satellite from persistence (no event emission).
     */
    public static Satellite reconstitute(
            SatelliteId id, String name, String owner, Orbit orbit,
            Telemetry telemetry, SatelliteStatus status,
            Instant registeredAt, Instant lastUpdatedAt) {
        Satellite satellite = new Satellite(id, name, owner, orbit, registeredAt);
        satellite.telemetry     = telemetry;
        satellite.status        = status;
        satellite.lastUpdatedAt = lastUpdatedAt;
        return satellite;
    }

    // ── Domain Behaviour ──────────────────────────────────────────────────────

    /**
     * Launches the satellite: transitions REGISTERED → ACTIVE and emits a domain event.
     */
    public void launch() {
        ensureCanTransitionTo(SatelliteStatus.ACTIVE);
        this.status        = SatelliteStatus.ACTIVE;
        this.lastUpdatedAt = Instant.now();
        domainEvents.add(new SatelliteLaunchedEvent(id, name, orbit, Instant.now()));
    }

    /**
     * Updates satellite telemetry. Automatically detects anomalies and
     * transitions status if needed.
     */
    public void updateTelemetry(Telemetry newTelemetry) {
        if (status == SatelliteStatus.DECOMMISSIONED) {
            throw new SatelliteDomainException(
                    "Cannot update telemetry for decommissioned satellite: " + id);
        }
        this.telemetry     = Objects.requireNonNull(newTelemetry, "Telemetry must not be null");
        this.lastUpdatedAt = Instant.now();

        if (newTelemetry.isAnomalous() && status != SatelliteStatus.ANOMALY) {
            if (status.canTransitionTo(SatelliteStatus.ANOMALY)) {
                this.status = SatelliteStatus.ANOMALY;
                domainEvents.add(new AnomalyDetectedEvent(
                        id, name, newTelemetry.anomalySummary(), Instant.now()));
            }
        } else if (!newTelemetry.isAnomalous() && status == SatelliteStatus.ANOMALY) {
            // Auto-recover to ACTIVE if telemetry normalises
            this.status = SatelliteStatus.ACTIVE;
        }
    }

    /**
     * Decommissions the satellite permanently.
     */
    public void decommission() {
        ensureCanTransitionTo(SatelliteStatus.DECOMMISSIONED);
        this.status        = SatelliteStatus.DECOMMISSIONED;
        this.lastUpdatedAt = Instant.now();
    }

    /**
     * Returns and clears collected domain events.
     * Called by application layer after successful persistence.
     */
    public List<Object> pullDomainEvents() {
        List<Object> events = Collections.unmodifiableList(new ArrayList<>(domainEvents));
        domainEvents.clear();
        return events;
    }

    // ── Private helpers ───────────────────────────────────────────────────────

    private void ensureCanTransitionTo(SatelliteStatus next) {
        if (!status.canTransitionTo(next)) {
            throw new SatelliteDomainException(
                    String.format("Cannot transition satellite '%s' from %s to %s", name, status, next));
        }
    }

    private static String validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Satellite name must not be blank");
        }
        if (name.length() > 100) {
            throw new IllegalArgumentException("Satellite name must not exceed 100 characters");
        }
        return name.trim();
    }

    // ── Accessors (no setters — immutable from outside) ───────────────────────
    public SatelliteId    getId()            { return id; }
    public String         getName()          { return name; }
    public String         getOwner()         { return owner; }
    public Orbit          getOrbit()         { return orbit; }
    public Telemetry      getTelemetry()     { return telemetry; }
    public SatelliteStatus getStatus()       { return status; }
    public Instant        getRegisteredAt()  { return registeredAt; }
    public Instant        getLastUpdatedAt() { return lastUpdatedAt; }
}
