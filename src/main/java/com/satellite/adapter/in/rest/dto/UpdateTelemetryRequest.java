package com.satellite.adapter.in.rest.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

/**
 * REST request DTO for telemetry updates.
 */
public record UpdateTelemetryRequest(
        @NotNull
        @DecimalMin("0.0") @DecimalMax("100.0")
        Double batteryPercentage,

        @NotNull
        Double signalStrengthDbm,

        @NotNull
        Double temperatureCelsius
) {}
