package com.satellite.adapter.in.rest;

import com.satellite.adapter.in.rest.dto.LaunchSatelliteRequest;
import com.satellite.adapter.in.rest.dto.SatelliteResponse;
import com.satellite.adapter.in.rest.dto.UpdateTelemetryRequest;
import com.satellite.adapter.in.rest.mapper.SatelliteRestMapper;
import com.satellite.domain.exception.SatelliteDomainException;
import com.satellite.domain.model.SatelliteId;
import com.satellite.domain.port.in.GetSatelliteUseCase;
import com.satellite.domain.port.in.LaunchSatelliteUseCase;
import com.satellite.domain.port.in.UpdateTelemetryUseCase;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Driving Adapter — REST Controller.
 *
 * <p>This class depends ONLY on domain ports (use cases), not on application services directly.
 * That is the key hexagonal constraint: adapters talk to ports, not implementations.
 *
 * <p>Handles HTTP concerns: status codes, request parsing, error responses.
 * Business logic stays in the domain.
 */
@RestController
@RequestMapping("/api/v1/satellites")
public class SatelliteController {

    private final LaunchSatelliteUseCase  launchUseCase;
    private final UpdateTelemetryUseCase  telemetryUseCase;
    private final GetSatelliteUseCase     getUseCase;

    public SatelliteController(LaunchSatelliteUseCase launchUseCase,
                               UpdateTelemetryUseCase telemetryUseCase,
                               GetSatelliteUseCase getUseCase) {
        this.launchUseCase   = launchUseCase;
        this.telemetryUseCase = telemetryUseCase;
        this.getUseCase       = getUseCase;
    }

    /**
     * POST /api/v1/satellites — Register and launch a new satellite.
     */
    @PostMapping
    public ResponseEntity<SatelliteResponse> launch(
            @Valid @RequestBody LaunchSatelliteRequest request) {
        var command  = SatelliteRestMapper.toCommand(request);
        var satellite = launchUseCase.launch(command);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(SatelliteRestMapper.toResponse(satellite));
    }

    /**
     * GET /api/v1/satellites — List all satellites.
     */
    @GetMapping
    public ResponseEntity<List<SatelliteResponse>> getAll() {
        List<SatelliteResponse> responses = getUseCase.getAll()
                .stream()
                .map(SatelliteRestMapper::toResponse)
                .toList();
        return ResponseEntity.ok(responses);
    }

    /**
     * GET /api/v1/satellites/{id} — Get a specific satellite.
     */
    @GetMapping("/{id}")
    public ResponseEntity<SatelliteResponse> getById(@PathVariable UUID id) {
        var satellite = getUseCase.getById(SatelliteId.of(id));
        return ResponseEntity.ok(SatelliteRestMapper.toResponse(satellite));
    }

    /**
     * PATCH /api/v1/satellites/{id}/telemetry — Update telemetry readings.
     */
    @PatchMapping("/{id}/telemetry")
    public ResponseEntity<SatelliteResponse> updateTelemetry(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateTelemetryRequest request) {
        var command  = SatelliteRestMapper.toCommand(id, request);
        var satellite = telemetryUseCase.updateTelemetry(command);
        return ResponseEntity.ok(SatelliteRestMapper.toResponse(satellite));
    }

    /**
     * Global exception handler for domain exceptions.
     */
    @ExceptionHandler(SatelliteDomainException.class)
    public ResponseEntity<ErrorResponse> handleDomainException(SatelliteDomainException ex) {
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(new ErrorResponse("DOMAIN_ERROR", ex.getMessage()));
    }

    /**
     * Handles IllegalArgumentException from Value Object constructors
     * (e.g., invalid orbit altitude, telemetry out of range).
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(new ErrorResponse("DOMAIN_ERROR", ex.getMessage()));
    }

    public record ErrorResponse(String code, String message) {}
}
