package com.satellite.infrastructure.adapter.in.rest.mapper;

import com.satellite.domain.model.Satellite;
import com.satellite.infrastructure.adapter.in.rest.dto.SatelliteResponse;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SatelliteRestMapper {

    public SatelliteResponse toResponse(Satellite satellite) {
        Double battery = satellite.getTelemetry() != null ? satellite.getTelemetry().batteryPercentage() : null;
        Double signal = satellite.getTelemetry() != null ? satellite.getTelemetry().signalStrengthDbm() : null;
        Double temp = satellite.getTelemetry() != null ? satellite.getTelemetry().temperatureCelsius() : null;
        boolean anomalous = satellite.getTelemetry() != null && satellite.getTelemetry().isAnomalous();

        return new SatelliteResponse(
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
                anomalous,
                satellite.getRegisteredAt(),
                satellite.getLastUpdatedAt()
        );
    }

    public List<SatelliteResponse> toResponseList(List<Satellite> satellites) {
        return satellites.stream().map(this::toResponse).toList();
    }
}
