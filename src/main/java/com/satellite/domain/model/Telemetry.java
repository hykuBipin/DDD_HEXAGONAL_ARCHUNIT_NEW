package com.satellite.domain.model;

/**
 * Telemetry Value Object — immutable snapshot of satellite health data.
 * Contains self-validating business rules for anomaly detection.
 */
public record Telemetry(
        double batteryPercentage,
        double signalStrengthDbm,
        double temperatureCelsius
) {

    // ── Business constants ────────────────────────────────────────────────────
    private static final double CRITICAL_BATTERY_THRESHOLD = 15.0;
    private static final double CRITICAL_SIGNAL_THRESHOLD  = -110.0;
    private static final double MAX_SAFE_TEMPERATURE        = 80.0;
    private static final double MIN_SAFE_TEMPERATURE        = -40.0;

    public Telemetry {
        if (batteryPercentage < 0 || batteryPercentage > 100) {
            throw new IllegalArgumentException(
                    "Battery percentage must be 0–100, got: " + batteryPercentage);
        }
        if (temperatureCelsius < -273.15) {
            throw new IllegalArgumentException(
                    "Temperature cannot be below absolute zero, got: " + temperatureCelsius);
        }
    }

    // ── Domain behaviour ──────────────────────────────────────────────────────

    /**
     * Returns true if any telemetry value indicates a potential anomaly.
     * This is domain logic — it belongs here, not in a service.
     */
    public boolean isAnomalous() {
        return isBatteryCritical() || isSignalLost() || isTemperatureOutOfRange();
    }

    public boolean isBatteryCritical() {
        return batteryPercentage < CRITICAL_BATTERY_THRESHOLD;
    }

    public boolean isSignalLost() {
        return signalStrengthDbm < CRITICAL_SIGNAL_THRESHOLD;
    }

    public boolean isTemperatureOutOfRange() {
        return temperatureCelsius > MAX_SAFE_TEMPERATURE
                || temperatureCelsius < MIN_SAFE_TEMPERATURE;
    }

    public String anomalySummary() {
        if (!isAnomalous()) return "NOMINAL";
        StringBuilder sb = new StringBuilder("ANOMALY[");
        if (isBatteryCritical()) sb.append("LOW_BATTERY,");
        if (isSignalLost()) sb.append("SIGNAL_LOST,");
        if (isTemperatureOutOfRange()) sb.append("TEMP_RANGE_EXCEEDED,");
        if (sb.charAt(sb.length() - 1) == ',') sb.deleteCharAt(sb.length() - 1);
        sb.append("]");
        return sb.toString();
    }
}
