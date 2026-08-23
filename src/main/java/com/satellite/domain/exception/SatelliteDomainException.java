package com.satellite.domain.exception;

/**
 * Root exception for all domain rule violations in the Satellite context.
 * Application and adapter layers should catch this and map to appropriate responses.
 */
public class SatelliteDomainException extends RuntimeException {

    public SatelliteDomainException(String message) {
        super(message);
    }

    public SatelliteDomainException(String message, Throwable cause) {
        super(message, cause);
    }
}
