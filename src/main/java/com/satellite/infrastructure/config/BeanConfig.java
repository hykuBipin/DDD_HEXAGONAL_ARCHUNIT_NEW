package com.satellite.infrastructure.config;

import com.satellite.application.service.GetSatelliteService;
import com.satellite.application.service.LaunchSatelliteService;
import com.satellite.application.service.UpdateTelemetryService;
import com.satellite.domain.port.in.GetSatelliteUseCase;
import com.satellite.domain.port.in.LaunchSatelliteUseCase;
import com.satellite.domain.port.in.UpdateTelemetryUseCase;
import com.satellite.domain.port.out.SatelliteEventPublisher;
import com.satellite.domain.port.out.SatelliteRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Spring Composition Root.
 *
 * <p>Wires together pure domain services and infrastructure adapters.
 * Application services are instantiated as Spring beans HERE in the infrastructure layer,
 * keeping the application and domain packages free of Spring annotations (@Service).
 */
@Configuration
public class BeanConfig {

    @Bean
    public LaunchSatelliteUseCase launchSatelliteUseCase(SatelliteRepository repository,
                                                         SatelliteEventPublisher eventPublisher) {
        return new LaunchSatelliteService(repository, eventPublisher);
    }

    @Bean
    public UpdateTelemetryUseCase updateTelemetryUseCase(SatelliteRepository repository,
                                                         SatelliteEventPublisher eventPublisher) {
        return new UpdateTelemetryService(repository, eventPublisher);
    }

    @Bean
    public GetSatelliteUseCase getSatelliteUseCase(SatelliteRepository repository) {
        return new GetSatelliteService(repository);
    }
}
