package com.satellite.domain.port.out;

/**
 * Driven Port (Outbound) — Event publishing abstraction.
 *
 * <p>The domain defines this interface so that application services
 * can publish domain events without knowing the messaging infrastructure.
 */
public interface SatelliteEventPublisher {

    /**
     * Publishes a domain event to whatever infrastructure is configured
     * (Spring Events, Kafka, RabbitMQ, etc.).
     *
     * @param event any domain event object
     */
    void publish(Object event);
}
