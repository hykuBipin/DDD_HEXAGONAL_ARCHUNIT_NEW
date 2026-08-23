package com.satellite.domain.model;

/**
 * Orbit Value Object — immutable, self-validating.
 * Represents the orbital parameters of a satellite.
 */
public record Orbit(double altitudeKm, double inclinationDegrees, OrbitType orbitType) {

    public enum OrbitType {
        LEO,  // Low Earth Orbit (< 2,000 km)
        MEO,  // Medium Earth Orbit (2,000–35,786 km)
        GEO,  // Geostationary Orbit (~35,786 km)
        HEO   // Highly Elliptical Orbit
    }

    public Orbit {
        if (altitudeKm <= 0) {
            throw new IllegalArgumentException("Altitude must be positive, got: " + altitudeKm);
        }
        if (inclinationDegrees < 0 || inclinationDegrees > 180) {
            throw new IllegalArgumentException(
                    "Inclination must be between 0 and 180 degrees, got: " + inclinationDegrees);
        }
        if (orbitType == null) {
            throw new IllegalArgumentException("OrbitType must not be null");
        }
        validateOrbitConsistency(altitudeKm, orbitType);
    }

    private static void validateOrbitConsistency(double altitudeKm, OrbitType orbitType) {
        switch (orbitType) {
            case LEO -> {
                if (altitudeKm >= 2000) {
                    throw new IllegalArgumentException(
                            "LEO altitude must be < 2000 km, got: " + altitudeKm);
                }
            }
            case GEO -> {
                if (altitudeKm < 35000 || altitudeKm > 36500) {
                    throw new IllegalArgumentException(
                            "GEO altitude must be ~35,786 km (±786 km), got: " + altitudeKm);
                }
            }
            case MEO -> {
                if (altitudeKm < 2000 || altitudeKm >= 35000) {
                    throw new IllegalArgumentException(
                            "MEO altitude must be between 2,000 and 35,000 km, got: " + altitudeKm);
                }
            }
            case HEO -> { /* HEO has no strict altitude constraint */ }
        }
    }

    public boolean isLowEarthOrbit() {
        return orbitType == OrbitType.LEO;
    }

    public boolean isGeostationary() {
        return orbitType == OrbitType.GEO;
    }
}
