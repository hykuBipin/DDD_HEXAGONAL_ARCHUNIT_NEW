package com.satellite.domain;

import com.satellite.domain.model.*;
import com.satellite.domain.exception.SatelliteDomainException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

/**
 * Domain unit tests — pure Java, zero Spring, zero Mockito needed.
 * The domain model can be tested in complete isolation.
 */
@DisplayName("Satellite Aggregate Root")
class SatelliteTest {

    private static final Orbit LEO_ORBIT = new Orbit(550.0, 53.0, Orbit.OrbitType.LEO);

    // ── Factory & Registration ────────────────────────────────────────────────

    @Nested
    @DisplayName("when registering")
    class Registration {

        @Test
        @DisplayName("should create satellite in REGISTERED status")
        void shouldBeRegisteredOnCreation() {
            Satellite satellite = Satellite.register("Starlink-99", "SpaceX", LEO_ORBIT);

            assertThat(satellite.getId()).isNotNull();
            assertThat(satellite.getName()).isEqualTo("Starlink-99");
            assertThat(satellite.getOwner()).isEqualTo("SpaceX");
            assertThat(satellite.getStatus()).isEqualTo(SatelliteStatus.REGISTERED);
            assertThat(satellite.getTelemetry()).isNull();
        }

        @Test
        @DisplayName("should reject blank name")
        void shouldRejectBlankName() {
            assertThatThrownBy(() -> Satellite.register("  ", "SpaceX", LEO_ORBIT))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("name must not be blank");
        }
    }

    // ── Launch ────────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("when launching")
    class Launch {

        @Test
        @DisplayName("should transition to ACTIVE and emit SatelliteLaunchedEvent")
        void shouldActivateAndEmitEvent() {
            Satellite satellite = Satellite.register("ISS-Mirror", "NASA", LEO_ORBIT);

            satellite.launch();

            assertThat(satellite.getStatus()).isEqualTo(SatelliteStatus.ACTIVE);
            var events = satellite.pullDomainEvents();
            assertThat(events).hasSize(1);
            assertThat(events.get(0))
                    .isInstanceOf(com.satellite.domain.event.SatelliteLaunchedEvent.class);
        }

        @Test
        @DisplayName("should not allow launching an already active satellite")
        void shouldNotAllowDoubleLaunch() {
            Satellite satellite = Satellite.register("ISS-Mirror", "NASA", LEO_ORBIT);
            satellite.launch();
            satellite.pullDomainEvents(); // clear events

            assertThatThrownBy(satellite::launch)
                    .isInstanceOf(SatelliteDomainException.class)
                    .hasMessageContaining("Cannot transition");
        }

        @Test
        @DisplayName("should clear events after pull")
        void shouldClearEventsAfterPull() {
            Satellite satellite = Satellite.register("ISS-Mirror", "NASA", LEO_ORBIT);
            satellite.launch();

            satellite.pullDomainEvents(); // first pull
            assertThat(satellite.pullDomainEvents()).isEmpty(); // second pull should be empty
        }
    }

    // ── Telemetry ─────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("when updating telemetry")
    class TelemetryUpdate {

        @Test
        @DisplayName("should detect anomaly and emit AnomalyDetectedEvent")
        void shouldEmitAnomalyEvent() {
            Satellite satellite = Satellite.register("WeatherSat-1", "ESA", LEO_ORBIT);
            satellite.launch();
            satellite.pullDomainEvents();

            // Critical battery below threshold
            Telemetry anomalousTelemetry = new Telemetry(10.0, -90.0, 25.0);
            satellite.updateTelemetry(anomalousTelemetry);

            assertThat(satellite.getStatus()).isEqualTo(SatelliteStatus.ANOMALY);
            var events = satellite.pullDomainEvents();
            assertThat(events).hasSize(1);
            assertThat(events.get(0))
                    .isInstanceOf(com.satellite.domain.event.AnomalyDetectedEvent.class);
        }

        @Test
        @DisplayName("should auto-recover to ACTIVE when telemetry normalises")
        void shouldRecoverFromAnomaly() {
            Satellite satellite = Satellite.register("WeatherSat-1", "ESA", LEO_ORBIT);
            satellite.launch();
            satellite.updateTelemetry(new Telemetry(10.0, -90.0, 25.0)); // anomaly
            satellite.pullDomainEvents();

            Telemetry normalTelemetry = new Telemetry(80.0, -70.0, 20.0);
            satellite.updateTelemetry(normalTelemetry);

            assertThat(satellite.getStatus()).isEqualTo(SatelliteStatus.ACTIVE);
        }

        @Test
        @DisplayName("should reject telemetry update on decommissioned satellite")
        void shouldRejectTelemetryForDecommissioned() {
            Satellite satellite = Satellite.register("OldSat", "ESA", LEO_ORBIT);
            satellite.launch();
            satellite.decommission();

            assertThatThrownBy(() -> satellite.updateTelemetry(new Telemetry(80.0, -70.0, 20.0)))
                    .isInstanceOf(SatelliteDomainException.class)
                    .hasMessageContaining("decommissioned");
        }
    }

    // ── Telemetry Value Object ────────────────────────────────────────────────

    @Nested
    @DisplayName("Telemetry value object")
    class TelemetryTests {

        @Test
        @DisplayName("should detect low battery anomaly")
        void shouldDetectLowBattery() {
            Telemetry telemetry = new Telemetry(12.0, -80.0, 22.0);
            assertThat(telemetry.isAnomalous()).isTrue();
            assertThat(telemetry.isBatteryCritical()).isTrue();
            assertThat(telemetry.anomalySummary()).contains("LOW_BATTERY");
        }

        @Test
        @DisplayName("should detect temperature out of range")
        void shouldDetectHighTemperature() {
            Telemetry telemetry = new Telemetry(80.0, -70.0, 95.0);
            assertThat(telemetry.isAnomalous()).isTrue();
            assertThat(telemetry.anomalySummary()).contains("TEMP_RANGE_EXCEEDED");
        }

        @Test
        @DisplayName("should report NOMINAL for healthy telemetry")
        void shouldReportNominal() {
            Telemetry telemetry = new Telemetry(75.0, -85.0, 22.0);
            assertThat(telemetry.isAnomalous()).isFalse();
            assertThat(telemetry.anomalySummary()).isEqualTo("NOMINAL");
        }
    }

    // ── Orbit Value Object ────────────────────────────────────────────────────

    @Nested
    @DisplayName("Orbit value object")
    class OrbitTests {

        @Test
        @DisplayName("should reject LEO orbit with altitude above 2000 km")
        void shouldRejectInvalidLeoAltitude() {
            assertThatThrownBy(() -> new Orbit(2500.0, 53.0, Orbit.OrbitType.LEO))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("LEO altitude");
        }

        @Test
        @DisplayName("should reject negative altitude")
        void shouldRejectNegativeAltitude() {
            assertThatThrownBy(() -> new Orbit(-100.0, 53.0, Orbit.OrbitType.MEO))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("should accept valid GEO orbit")
        void shouldAcceptValidGeoOrbit() {
            Orbit orbit = new Orbit(35786.0, 0.0, Orbit.OrbitType.GEO);
            assertThat(orbit.isGeostationary()).isTrue();
        }
    }
}
