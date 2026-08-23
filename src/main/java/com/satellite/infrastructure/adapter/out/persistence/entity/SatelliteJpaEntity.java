package com.satellite.infrastructure.adapter.out.persistence.entity;

import com.satellite.domain.model.Orbit;
import com.satellite.domain.model.SatelliteStatus;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

/**
 * JPA Entity — infrastructure concern only.
 *
 * <p>Deliberately SEPARATE from the domain Satellite class.
 * JPA annotations, database column names, and nullable constraints
 * are infrastructure details that must NOT leak into the domain.
 */
@Entity
@Table(name = "satellites")
public class SatelliteJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "owner", nullable = false)
    private String owner;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private SatelliteStatus status;

    // Orbit embedded as flat columns
    @Column(name = "orbit_altitude_km", nullable = false)
    private double orbitAltitudeKm;

    @Column(name = "orbit_inclination_deg", nullable = false)
    private double orbitInclinationDegrees;

    @Column(name = "orbit_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private Orbit.OrbitType orbitType;

    // Telemetry — nullable (not present before first update)
    @Column(name = "telemetry_battery")
    private Double telemetryBattery;

    @Column(name = "telemetry_signal")
    private Double telemetrySignal;

    @Column(name = "telemetry_temperature")
    private Double telemetryTemperature;

    @Column(name = "registered_at", nullable = false, updatable = false)
    private Instant registeredAt;

    @Column(name = "last_updated_at", nullable = false)
    private Instant lastUpdatedAt;

    // ── JPA requires no-arg constructor ──────────────────────────────────────
    protected SatelliteJpaEntity() {}

    public SatelliteJpaEntity(UUID id, String name, String owner, SatelliteStatus status,
                              double orbitAltitudeKm, double orbitInclinationDegrees,
                              Orbit.OrbitType orbitType, Double telemetryBattery,
                              Double telemetrySignal, Double telemetryTemperature,
                              Instant registeredAt, Instant lastUpdatedAt) {
        this.id                      = id;
        this.name                    = name;
        this.owner                   = owner;
        this.status                  = status;
        this.orbitAltitudeKm         = orbitAltitudeKm;
        this.orbitInclinationDegrees = orbitInclinationDegrees;
        this.orbitType               = orbitType;
        this.telemetryBattery        = telemetryBattery;
        this.telemetrySignal         = telemetrySignal;
        this.telemetryTemperature    = telemetryTemperature;
        this.registeredAt            = registeredAt;
        this.lastUpdatedAt           = lastUpdatedAt;
    }

    // ── Getters ───────────────────────────────────────────────────────────────
    public UUID getId() { return id; }
    public String getName() { return name; }
    public String getOwner() { return owner; }
    public SatelliteStatus getStatus() { return status; }
    public double getOrbitAltitudeKm() { return orbitAltitudeKm; }
    public double getOrbitInclinationDegrees() { return orbitInclinationDegrees; }
    public Orbit.OrbitType getOrbitType() { return orbitType; }
    public Double getTelemetryBattery() { return telemetryBattery; }
    public Double getTelemetrySignal() { return telemetrySignal; }
    public Double getTelemetryTemperature() { return telemetryTemperature; }
    public Instant getRegisteredAt() { return registeredAt; }
    public Instant getLastUpdatedAt() { return lastUpdatedAt; }
}
