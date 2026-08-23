package com.satellite.adapter.out.persistence.mapper;

import com.satellite.adapter.out.persistence.entity.SatelliteJpaEntity;
import com.satellite.domain.model.*;

/**
 * Manual mapper between domain Satellite and SatelliteJpaEntity.
 *
 * <p>We use a manual mapper here instead of MapStruct to keep the
 * mapping logic explicit and visible. MapStruct would work equally well.
 *
 * <p>Key insight: the mapper lives in the ADAPTER layer, not the domain.
 */
public class SatellitePersistenceMapper {

    private SatellitePersistenceMapper() {}

    public static SatelliteJpaEntity toJpaEntity(Satellite satellite) {
        SatelliteJpaEntity entity = new SatelliteJpaEntity();
        entity.setId(satellite.getId().value());
        entity.setName(satellite.getName());
        entity.setOwner(satellite.getOwner());
        entity.setStatus(satellite.getStatus());
        entity.setOrbitAltitudeKm(satellite.getOrbit().altitudeKm());
        entity.setOrbitInclinationDegrees(satellite.getOrbit().inclinationDegrees());
        entity.setOrbitType(satellite.getOrbit().orbitType());
        entity.setRegisteredAt(satellite.getRegisteredAt());
        entity.setLastUpdatedAt(satellite.getLastUpdatedAt());

        if (satellite.getTelemetry() != null) {
            entity.setTelemetryBattery(satellite.getTelemetry().batteryPercentage());
            entity.setTelemetrySignal(satellite.getTelemetry().signalStrengthDbm());
            entity.setTelemetryTemperature(satellite.getTelemetry().temperatureCelsius());
        }

        return entity;
    }

    public static Satellite toDomain(SatelliteJpaEntity entity) {
        Orbit orbit = new Orbit(
                entity.getOrbitAltitudeKm(),
                entity.getOrbitInclinationDegrees(),
                entity.getOrbitType()
        );

        Telemetry telemetry = null;
        if (entity.getTelemetryBattery() != null) {
            telemetry = new Telemetry(
                    entity.getTelemetryBattery(),
                    entity.getTelemetrySignal(),
                    entity.getTelemetryTemperature()
            );
        }

        return Satellite.reconstitute(
                SatelliteId.of(entity.getId()),
                entity.getName(),
                entity.getOwner(),
                orbit,
                telemetry,
                entity.getStatus(),
                entity.getRegisteredAt(),
                entity.getLastUpdatedAt()
        );
    }
}
