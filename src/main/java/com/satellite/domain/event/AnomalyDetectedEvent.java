package com.satellite.domain.event;

import com.satellite.domain.model.SatelliteId;

import java.time.Instant;

/**
 * Domain Event — raised when satellite telemetry indicates an anomaly.
 */
public record AnomalyDetectedEvent(
        SatelliteId satelliteId,
        String satelliteName,
        String anomalySummary,
        Instant occurredAt
) {}
