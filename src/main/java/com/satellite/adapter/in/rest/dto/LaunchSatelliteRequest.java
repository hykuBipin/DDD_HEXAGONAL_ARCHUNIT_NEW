package com.satellite.adapter.in.rest.dto;

import com.satellite.domain.model.Orbit;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * REST request DTO — carries HTTP input for launching a satellite.
 * Validation annotations belong here (adapter concern), NOT on domain objects.
 */
public record LaunchSatelliteRequest(
        @NotBlank(message = "Satellite name is required")
        String name,

        @NotBlank(message = "Owner is required")
        String owner,

        @NotNull(message = "Orbit is required")
        OrbitRequest orbit
) {
    public record OrbitRequest(
            @Positive(message = "Altitude must be positive")
            double altitudeKm,

            double inclinationDegrees,

            @NotNull(message = "Orbit type is required")
            Orbit.OrbitType orbitType
    ) {}
}
