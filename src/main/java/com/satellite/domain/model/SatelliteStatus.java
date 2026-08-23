package com.satellite.domain.model;

/**
 * Lifecycle states of a satellite.
 * Transitions are enforced by the Satellite aggregate root.
 */
public enum SatelliteStatus {
    /**
     * Satellite has been registered but not yet launched.
     */
    REGISTERED,

    /**
     * Satellite is operational and transmitting telemetry.
     */
    ACTIVE,

    /**
     * Satellite is not currently transmitting but still operational.
     */
    INACTIVE,

    /**
     * Satellite has reported anomalous telemetry readings.
     */
    ANOMALY,

    /**
     * Satellite has been permanently retired.
     */
    DECOMMISSIONED;

    public boolean canTransitionTo(SatelliteStatus next) {
        return switch (this) {
            case REGISTERED    -> next == ACTIVE;
            case ACTIVE        -> next == INACTIVE || next == ANOMALY || next == DECOMMISSIONED;
            case INACTIVE      -> next == ACTIVE   || next == DECOMMISSIONED;
            case ANOMALY       -> next == ACTIVE   || next == DECOMMISSIONED;
            case DECOMMISSIONED -> false; // terminal state
        };
    }
}
