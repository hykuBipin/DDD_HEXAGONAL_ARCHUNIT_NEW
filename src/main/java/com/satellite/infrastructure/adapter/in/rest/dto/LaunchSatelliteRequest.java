package com.satellite.infrastructure.adapter.in.rest.dto;

import com.satellite.domain.model.Orbit;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record LaunchSatelliteRequest(
        @NotBlank(message = "Satellite name must not be blank")
        String name,

        @NotBlank(message = "Owner must not be blank")
        String owner,

        @Positive(message = "Altitude must be positive")
        double altitudeKm,

        double inclinationDegrees,

        @NotNull(message = "Orbit type must be specified")
        Orbit.OrbitType orbitType
) {}
