package com.satellite.domain.port.in;

import com.satellite.domain.model.Satellite;
import com.satellite.domain.model.Telemetry;

import java.util.UUID;

/**
 * Driving Port — Update satellite telemetry and detect anomalies.
 */
public interface UpdateTelemetryUseCase {

    record TelemetryCommand(UUID satelliteId, Telemetry telemetry) {}

    /**
     * Updates the telemetry reading for an active satellite.
     *
     * @param command the satellite ID and new telemetry snapshot
     * @return updated satellite state
     */
    Satellite updateTelemetry(TelemetryCommand command);
}
