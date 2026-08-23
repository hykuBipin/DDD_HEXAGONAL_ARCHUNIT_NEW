package com.satellite.domain.model;

import java.util.Objects;
import java.util.UUID;

/**
 * Strongly-typed Value Object for Satellite identity.
 * Wraps UUID to prevent primitive obsession and enforce type safety.
 */
public record SatelliteId(UUID value) {

    public SatelliteId {
        Objects.requireNonNull(value, "SatelliteId value must not be null");
    }

    public static SatelliteId generate() {
        return new SatelliteId(UUID.randomUUID());
    }

    public static SatelliteId of(String uuidString) {
        return new SatelliteId(UUID.fromString(uuidString));
    }

    public static SatelliteId of(UUID uuid) {
        return new SatelliteId(uuid);
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
