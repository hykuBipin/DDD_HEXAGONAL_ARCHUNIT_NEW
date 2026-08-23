package com.satellite.adapter.in.rest.dto;

import com.satellite.domain.model.Orbit;
import com.satellite.domain.model.SatelliteStatus;

import java.time.Instant;
import java.util.UUID;

/**
 * REST response DTO — carries satellite data to the HTTP client.
 *
 * <p>We never expose domain objects directly through the REST layer.
 * DTOs decouple the API contract from the domain model.
 */
public record SatelliteResponse(
        UUID id,
        String name,
        String owner,
        SatelliteStatus status,
        OrbitResponse orbit,
        TelemetryResponse telemetry,
        Instant registeredAt,
        Instant lastUpdatedAt
) {
    public record OrbitResponse(
            double altitudeKm,
            double inclinationDegrees,
            Orbit.OrbitType orbitType
    ) {}

    public record TelemetryResponse(
            double batteryPercentage,
            double signalStrengthDbm,
            double temperatureCelsius,
            boolean anomalous,
            String anomalySummary
    ) {}
}
