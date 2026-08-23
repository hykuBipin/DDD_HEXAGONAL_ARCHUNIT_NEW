package com.satellite.infrastructure.adapter.in.rest.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public record UpdateTelemetryRequest(
        @Min(value = 0, message = "Battery percentage cannot be less than 0")
        @Max(value = 100, message = "Battery percentage cannot be greater than 100")
        double batteryPercentage,

        double signalStrengthDbm,

        @Min(value = -273, message = "Temperature cannot be below absolute zero")
        double temperatureCelsius
) {}
