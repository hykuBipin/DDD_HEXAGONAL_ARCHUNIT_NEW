package com.satellite.adapter.out.persistence.entity;

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
    public SatelliteJpaEntity() {}

    // ── Getters & Setters (JPA needs them) ───────────────────────────────────
    public UUID getId()                           { return id; }
    public void setId(UUID id)                    { this.id = id; }
    public String getName()                       { return name; }
    public void setName(String name)              { this.name = name; }
    public String getOwner()                      { return owner; }
    public void setOwner(String owner)            { this.owner = owner; }
    public SatelliteStatus getStatus()            { return status; }
    public void setStatus(SatelliteStatus status) { this.status = status; }
    public double getOrbitAltitudeKm()            { return orbitAltitudeKm; }
    public void setOrbitAltitudeKm(double v)      { this.orbitAltitudeKm = v; }
    public double getOrbitInclinationDegrees()    { return orbitInclinationDegrees; }
    public void setOrbitInclinationDegrees(double v) { this.orbitInclinationDegrees = v; }
    public Orbit.OrbitType getOrbitType()         { return orbitType; }
    public void setOrbitType(Orbit.OrbitType t)   { this.orbitType = t; }
    public Double getTelemetryBattery()           { return telemetryBattery; }
    public void setTelemetryBattery(Double v)     { this.telemetryBattery = v; }
    public Double getTelemetrySignal()            { return telemetrySignal; }
    public void setTelemetrySignal(Double v)      { this.telemetrySignal = v; }
    public Double getTelemetryTemperature()       { return telemetryTemperature; }
    public void setTelemetryTemperature(Double v) { this.telemetryTemperature = v; }
    public Instant getRegisteredAt()              { return registeredAt; }
    public void setRegisteredAt(Instant v)        { this.registeredAt = v; }
    public Instant getLastUpdatedAt()             { return lastUpdatedAt; }
    public void setLastUpdatedAt(Instant v)       { this.lastUpdatedAt = v; }
}
