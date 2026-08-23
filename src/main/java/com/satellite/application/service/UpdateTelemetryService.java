package com.satellite.application.service;

import com.satellite.domain.exception.SatelliteDomainException;
import com.satellite.domain.model.Satellite;
import com.satellite.domain.model.SatelliteId;
import com.satellite.domain.port.in.UpdateTelemetryUseCase;
import com.satellite.domain.port.out.SatelliteEventPublisher;
import com.satellite.domain.port.out.SatelliteRepository;

/**
 * Application Service — implements UpdateTelemetryUseCase.
 */
public class UpdateTelemetryService implements UpdateTelemetryUseCase {

    private final SatelliteRepository    repository;
    private final SatelliteEventPublisher eventPublisher;

    public UpdateTelemetryService(SatelliteRepository repository,
                                  SatelliteEventPublisher eventPublisher) {
        this.repository     = repository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public Satellite updateTelemetry(TelemetryCommand command) {
        SatelliteId id = SatelliteId.of(command.satelliteId());

        Satellite satellite = repository.findById(id)
                .orElseThrow(() -> new SatelliteDomainException(
                        "Satellite not found: " + command.satelliteId()));

        // Domain logic lives in the aggregate
        satellite.updateTelemetry(command.telemetry());

        Satellite saved = repository.save(satellite);

        // Publish anomaly events if any were raised
        saved.pullDomainEvents().forEach(eventPublisher::publish);

        return saved;
    }
}
