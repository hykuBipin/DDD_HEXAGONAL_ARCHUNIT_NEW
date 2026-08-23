package com.satellite.application;

import com.satellite.domain.exception.SatelliteDomainException;
import com.satellite.domain.model.*;
import com.satellite.domain.port.in.LaunchSatelliteUseCase;
import com.satellite.domain.port.in.UpdateTelemetryUseCase;
import com.satellite.domain.port.out.SatelliteEventPublisher;
import com.satellite.domain.port.out.SatelliteRepository;
import com.satellite.application.service.GetSatelliteService;
import com.satellite.application.service.LaunchSatelliteService;
import com.satellite.application.service.UpdateTelemetryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Application service tests — use Mockito to stub ports (not real adapters).
 * Tests the orchestration logic, event publishing, and error handling.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Application Services")
class ApplicationServiceTest {

    @Mock SatelliteRepository    repository;
    @Mock SatelliteEventPublisher eventPublisher;

    private static final Orbit LEO = new Orbit(550.0, 53.0, Orbit.OrbitType.LEO);

    // ── LaunchSatelliteService ─────────────────────────────────────────────────

    @Test
    @DisplayName("LaunchSatelliteService: should save satellite and publish launch event")
    void launchShouldSaveAndPublishEvent() {
        LaunchSatelliteService service = new LaunchSatelliteService(repository, eventPublisher);

        // Given: repository returns the saved satellite
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        // When
        var command  = new LaunchSatelliteUseCase.LaunchCommand("Starlink-100", "SpaceX", LEO);
        Satellite result = service.launch(command);

        // Then
        assertThat(result.getStatus()).isEqualTo(SatelliteStatus.ACTIVE);
        verify(repository, times(1)).save(any(Satellite.class));
        // Event should be published (anomaly or launch)
        verify(eventPublisher, atLeastOnce()).publish(any());
    }

    // ── UpdateTelemetryService ─────────────────────────────────────────────────

    @Test
    @DisplayName("UpdateTelemetryService: should publish anomaly event on critical telemetry")
    void shouldPublishAnomalyEvent() {
        UpdateTelemetryService service = new UpdateTelemetryService(repository, eventPublisher);

        Satellite satellite = Satellite.reconstitute(
                SatelliteId.generate(), "WeatherSat", "ESA", LEO,
                null, SatelliteStatus.ACTIVE, Instant.now(), Instant.now());

        UUID satelliteId = satellite.getId().value();
        when(repository.findById(any())).thenReturn(Optional.of(satellite));
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        var command = new UpdateTelemetryUseCase.TelemetryCommand(
                satelliteId, new Telemetry(5.0, -120.0, 25.0));

        Satellite result = service.updateTelemetry(command);

        assertThat(result.getStatus()).isEqualTo(SatelliteStatus.ANOMALY);
        verify(eventPublisher, atLeastOnce()).publish(any());
    }

    @Test
    @DisplayName("UpdateTelemetryService: should throw when satellite not found")
    void shouldThrowWhenNotFound() {
        UpdateTelemetryService service = new UpdateTelemetryService(repository, eventPublisher);
        when(repository.findById(any())).thenReturn(Optional.empty());

        var command = new UpdateTelemetryUseCase.TelemetryCommand(
                UUID.randomUUID(), new Telemetry(80.0, -70.0, 22.0));

        assertThatThrownBy(() -> service.updateTelemetry(command))
                .isInstanceOf(SatelliteDomainException.class)
                .hasMessageContaining("not found");
    }

    // ── GetSatelliteService ───────────────────────────────────────────────────

    @Test
    @DisplayName("GetSatelliteService: should return all satellites")
    void shouldReturnAll() {
        GetSatelliteService service = new GetSatelliteService(repository);

        Satellite s1 = Satellite.register("Sat-A", "ESA", LEO);
        Satellite s2 = Satellite.register("Sat-B", "NASA", LEO);
        when(repository.findAll()).thenReturn(List.of(s1, s2));

        List<Satellite> result = service.getAll();

        assertThat(result).hasSize(2);
    }

    @Test
    @DisplayName("GetSatelliteService: should throw on missing ID")
    void shouldThrowOnMissingId() {
        GetSatelliteService service = new GetSatelliteService(repository);
        when(repository.findById(any())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getById(SatelliteId.generate()))
                .isInstanceOf(SatelliteDomainException.class);
    }
}
