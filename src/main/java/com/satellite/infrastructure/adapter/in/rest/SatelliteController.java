package com.satellite.infrastructure.adapter.in.rest;

import com.satellite.domain.model.Orbit;
import com.satellite.domain.model.Satellite;
import com.satellite.domain.model.SatelliteId;
import com.satellite.domain.model.Telemetry;
import com.satellite.domain.port.in.GetSatelliteUseCase;
import com.satellite.domain.port.in.LaunchSatelliteUseCase;
import com.satellite.domain.port.in.UpdateTelemetryUseCase;
import com.satellite.infrastructure.adapter.in.rest.dto.LaunchSatelliteRequest;
import com.satellite.infrastructure.adapter.in.rest.dto.SatelliteResponse;
import com.satellite.infrastructure.adapter.in.rest.dto.UpdateTelemetryRequest;
import com.satellite.infrastructure.adapter.in.rest.mapper.SatelliteRestMapper;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Driving Adapter (Inbound REST Controller).
 *
 * <p>Receives HTTP requests, converts them into Domain UseCase calls,
 * and maps domain entities to REST responses.
 *
 * <p>CRITICAL ARCHITECTURAL RULE:
 * This controller depends ONLY on Driving Ports (interfaces in domain.port.in)
 * and never on Application Services directly or DB entities.
 */
@RestController
@RequestMapping("/api/v1/satellites")
public class SatelliteController {

    private final LaunchSatelliteUseCase launchSatelliteUseCase;
    private final UpdateTelemetryUseCase updateTelemetryUseCase;
    private final GetSatelliteUseCase getSatelliteUseCase;
    private final SatelliteRestMapper mapper;

    public SatelliteController(LaunchSatelliteUseCase launchSatelliteUseCase,
                               UpdateTelemetryUseCase updateTelemetryUseCase,
                               GetSatelliteUseCase getSatelliteUseCase,
                               SatelliteRestMapper mapper) {
        this.launchSatelliteUseCase = launchSatelliteUseCase;
        this.updateTelemetryUseCase = updateTelemetryUseCase;
        this.getSatelliteUseCase = getSatelliteUseCase;
        this.mapper = mapper;
    }

    @PostMapping
    public ResponseEntity<SatelliteResponse> launchSatellite(
            @Valid @RequestBody LaunchSatelliteRequest request) {

        Orbit orbit = new Orbit(request.altitudeKm(), request.inclinationDegrees(), request.orbitType());
        LaunchSatelliteUseCase.LaunchCommand command =
                new LaunchSatelliteUseCase.LaunchCommand(request.name(), request.owner(), orbit);

        Satellite launched = launchSatelliteUseCase.launch(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toResponse(launched));
    }

    @PutMapping("/{id}/telemetry")
    public ResponseEntity<SatelliteResponse> updateTelemetry(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateTelemetryRequest request) {

        Telemetry telemetry = new Telemetry(
                request.batteryPercentage(),
                request.signalStrengthDbm(),
                request.temperatureCelsius()
        );

        UpdateTelemetryUseCase.TelemetryCommand command =
                new UpdateTelemetryUseCase.TelemetryCommand(id, telemetry);

        Satellite updated = updateTelemetryUseCase.updateTelemetry(command);
        return ResponseEntity.ok(mapper.toResponse(updated));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SatelliteResponse> getSatellite(@PathVariable UUID id) {
        Satellite satellite = getSatelliteUseCase.getById(SatelliteId.of(id));
        return ResponseEntity.ok(mapper.toResponse(satellite));
    }

    @GetMapping
    public ResponseEntity<List<SatelliteResponse>> getAllSatellites() {
        List<Satellite> satellites = getSatelliteUseCase.getAll();
        return ResponseEntity.ok(mapper.toResponseList(satellites));
    }
}
