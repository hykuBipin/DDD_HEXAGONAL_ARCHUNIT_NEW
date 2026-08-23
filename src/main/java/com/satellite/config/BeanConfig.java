package com.satellite.config;

import com.satellite.adapter.in.rest.SatelliteController;
import com.satellite.adapter.out.messaging.SatelliteEventPublisherAdapter;
import com.satellite.adapter.out.persistence.SatelliteJpaAdapter;
import com.satellite.adapter.out.persistence.entity.SatelliteJpaRepository;
import com.satellite.application.service.GetSatelliteService;
import com.satellite.application.service.LaunchSatelliteService;
import com.satellite.application.service.UpdateTelemetryService;
import com.satellite.domain.event.AnomalyDetectedEvent;
import com.satellite.domain.event.SatelliteLaunchedEvent;
import com.satellite.domain.port.out.SatelliteEventPublisher;
import com.satellite.domain.port.out.SatelliteRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Spring Bean Configuration — the composition root.
 *
 * <p>This is the ONLY place where adapters are wired to ports and
 * application services. The hexagonal architecture is assembled here.
 *
 * <p>Notice: domain objects have NO Spring annotations.
 * Application services have NO Spring annotations (plain classes).
 * Only the config and adapter classes know about Spring.
 */
@Configuration
@EnableTransactionManagement
public class BeanConfig {

    private static final Logger log = LoggerFactory.getLogger(BeanConfig.class);

    // ── Driven Adapter Beans ─────────────────────────────────────────────────

    @Bean
    public SatelliteRepository satelliteRepository(SatelliteJpaRepository jpaRepository) {
        return new SatelliteJpaAdapter(jpaRepository);
    }

    @Bean
    public SatelliteEventPublisher satelliteEventPublisher(
            ApplicationEventPublisher springPublisher) {
        return new SatelliteEventPublisherAdapter(springPublisher);
    }

    // ── Application Service Beans ────────────────────────────────────────────

    @Bean
    public LaunchSatelliteService launchSatelliteService(
            SatelliteRepository repository,
            SatelliteEventPublisher eventPublisher) {
        return new LaunchSatelliteService(repository, eventPublisher);
    }

    @Bean
    public UpdateTelemetryService updateTelemetryService(
            SatelliteRepository repository,
            SatelliteEventPublisher eventPublisher) {
        return new UpdateTelemetryService(repository, eventPublisher);
    }

    @Bean
    public GetSatelliteService getSatelliteService(SatelliteRepository repository) {
        return new GetSatelliteService(repository);
    }

    // ── Event Listeners (demonstration) ─────────────────────────────────────

    @EventListener
    public void onSatelliteLaunched(SatelliteLaunchedEvent event) {
        log.info("🚀 Satellite launched: {} in orbit {} at altitude {} km",
                event.satelliteName(),
                event.orbit().orbitType(),
                event.orbit().altitudeKm());
    }

    @EventListener
    public void onAnomalyDetected(AnomalyDetectedEvent event) {
        log.warn("⚠️  Anomaly detected for satellite [{}]: {}",
                event.satelliteName(),
                event.anomalySummary());
    }
}
