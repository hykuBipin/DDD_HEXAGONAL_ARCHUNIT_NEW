package com.satellite.application.service;

import com.satellite.domain.model.Satellite;
import com.satellite.domain.port.in.LaunchSatelliteUseCase;
import com.satellite.domain.port.out.SatelliteEventPublisher;
import com.satellite.domain.port.out.SatelliteRepository;

/**
 * Application Service — implements the LaunchSatelliteUseCase driving port.
 *
 * <p>Responsibilities:
 * <ul>
 *   <li>Orchestrates domain objects (does NOT contain business logic itself)</li>
 *   <li>Manages transactions (via Spring @Transactional in the config layer)</li>
 *   <li>Publishes domain events after successful persistence</li>
 * </ul>
 *
 * <p>This class depends on domain ports, NOT on concrete adapters.
 * The hexagon is never broken.
 */
public class LaunchSatelliteService implements LaunchSatelliteUseCase {

    private final SatelliteRepository   repository;
    private final SatelliteEventPublisher eventPublisher;

    public LaunchSatelliteService(SatelliteRepository repository,
                                  SatelliteEventPublisher eventPublisher) {
        this.repository     = repository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public Satellite launch(LaunchCommand command) {
        // 1. Create and launch the aggregate (domain logic inside Satellite)
        Satellite satellite = Satellite.register(command.name(), command.owner(), command.orbit());
        satellite.launch();

        // 2. Persist via outbound port (repository)
        Satellite saved = repository.save(satellite);

        // 3. Publish collected domain events
        saved.pullDomainEvents().forEach(eventPublisher::publish);

        return saved;
    }
}
