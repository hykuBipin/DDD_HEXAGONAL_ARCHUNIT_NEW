package com.satellite.infrastructure.adapter.out.messaging;

import com.satellite.domain.port.out.SatelliteEventPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

/**
 * Driven Adapter (Outbound Messaging Adapter).
 *
 * <p>Implements the SatelliteEventPublisher outbound port interface.
 * Delegates domain event publishing to Spring's ApplicationEventPublisher,
 * keeping the domain core decoupled from Spring infrastructure.
 */
@Component
public class SatelliteEventPublisherAdapter implements SatelliteEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(SatelliteEventPublisherAdapter.class);

    private final ApplicationEventPublisher springEventPublisher;

    public SatelliteEventPublisherAdapter(ApplicationEventPublisher springEventPublisher) {
        this.springEventPublisher = springEventPublisher;
    }

    @Override
    public void publish(Object event) {
        log.info("Publishing domain event via Spring ApplicationEventPublisher: {}", event);
        springEventPublisher.publishEvent(event);
    }
}
