package com.satellite.infrastructure.adapter.out.persistence.mapper;

import com.satellite.domain.model.*;
import com.satellite.infrastructure.adapter.out.persistence.entity.SatelliteJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class SatellitePersistenceMapper {

    public SatelliteJpaEntity toJpaEntity(Satellite satellite) {
        Double battery = satellite.getTelemetry() != null ? satellite.getTelemetry().batteryPercentage() : null;
        Double signal = satellite.getTelemetry() != null ? satellite.getTelemetry().signalStrengthDbm() : null;
        Double temp = satellite.getTelemetry() != null ? satellite.getTelemetry().temperatureCelsius() : null;

        return new SatelliteJpaEntity(
                satellite.getId().value(),
                satellite.getName(),
                satellite.getOwner(),
                satellite.getStatus(),
                satellite.getOrbit().altitudeKm(),
                satellite.getOrbit().inclinationDegrees(),
                satellite.getOrbit().orbitType(),
                battery,
                signal,
                temp,
                satellite.getRegisteredAt(),
                satellite.getLastUpdatedAt()
        );
    }

    public Satellite toDomainEntity(SatelliteJpaEntity entity) {
        Orbit orbit = new Orbit(
                entity.getOrbitAltitudeKm(),
                entity.getOrbitInclinationDegrees(),
                entity.getOrbitType()
        );

        Telemetry telemetry = null;
        if (entity.getTelemetryBattery() != null
                && entity.getTelemetrySignal() != null
                && entity.getTelemetryTemperature() != null) {
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
