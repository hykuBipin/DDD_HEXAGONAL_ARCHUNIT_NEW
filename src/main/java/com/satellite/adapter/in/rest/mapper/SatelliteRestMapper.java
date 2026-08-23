package com.satellite.adapter.in.rest.mapper;

import com.satellite.adapter.in.rest.dto.LaunchSatelliteRequest;
import com.satellite.adapter.in.rest.dto.SatelliteResponse;
import com.satellite.adapter.in.rest.dto.UpdateTelemetryRequest;
import com.satellite.domain.model.Orbit;
import com.satellite.domain.model.Satellite;
import com.satellite.domain.model.Telemetry;
import com.satellite.domain.port.in.LaunchSatelliteUseCase;
import com.satellite.domain.port.in.UpdateTelemetryUseCase;

import java.util.UUID;

/**
 * REST ↔ Domain mapper — converts between HTTP DTOs and domain commands/objects.
 * Lives in the adapter layer. Domain layer never references this class.
 */
public class SatelliteRestMapper {

    private SatelliteRestMapper() {}

    public static LaunchSatelliteUseCase.LaunchCommand toCommand(LaunchSatelliteRequest request) {
        Orbit orbit = new Orbit(
                request.orbit().altitudeKm(),
                request.orbit().inclinationDegrees(),
                request.orbit().orbitType()
        );
        return new LaunchSatelliteUseCase.LaunchCommand(request.name(), request.owner(), orbit);
    }

    public static UpdateTelemetryUseCase.TelemetryCommand toCommand(
            UUID satelliteId, UpdateTelemetryRequest request) {
        Telemetry telemetry = new Telemetry(
                request.batteryPercentage(),
                request.signalStrengthDbm(),
                request.temperatureCelsius()
        );
        return new UpdateTelemetryUseCase.TelemetryCommand(satelliteId, telemetry);
    }

    public static SatelliteResponse toResponse(Satellite satellite) {
        SatelliteResponse.OrbitResponse orbit = new SatelliteResponse.OrbitResponse(
                satellite.getOrbit().altitudeKm(),
                satellite.getOrbit().inclinationDegrees(),
                satellite.getOrbit().orbitType()
        );

        SatelliteResponse.TelemetryResponse telemetry = null;
        if (satellite.getTelemetry() != null) {
            telemetry = new SatelliteResponse.TelemetryResponse(
                    satellite.getTelemetry().batteryPercentage(),
                    satellite.getTelemetry().signalStrengthDbm(),
                    satellite.getTelemetry().temperatureCelsius(),
                    satellite.getTelemetry().isAnomalous(),
                    satellite.getTelemetry().anomalySummary()
            );
        }

        return new SatelliteResponse(
                satellite.getId().value(),
                satellite.getName(),
                satellite.getOwner(),
                satellite.getStatus(),
                orbit,
                telemetry,
                satellite.getRegisteredAt(),
                satellite.getLastUpdatedAt()
        );
    }
}
