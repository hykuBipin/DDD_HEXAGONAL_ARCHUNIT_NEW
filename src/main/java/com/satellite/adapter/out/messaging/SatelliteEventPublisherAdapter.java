package com.satellite.adapter.out.messaging;

import com.satellite.domain.port.out.SatelliteEventPublisher;
import org.springframework.context.ApplicationEventPublisher;

/**
 * Driven Adapter — publishes domain events via Spring's ApplicationEventPublisher.
 *
 * <p>In a production system, this could be replaced by a Kafka or RabbitMQ adapter
 * without changing any domain or application code — the hexagon remains intact.
 */
public class SatelliteEventPublisherAdapter implements SatelliteEventPublisher {

    private final ApplicationEventPublisher springEventPublisher;

    public SatelliteEventPublisherAdapter(ApplicationEventPublisher springEventPublisher) {
        this.springEventPublisher = springEventPublisher;
    }

    @Override
    public void publish(Object event) {
        springEventPublisher.publishEvent(event);
    }
}
