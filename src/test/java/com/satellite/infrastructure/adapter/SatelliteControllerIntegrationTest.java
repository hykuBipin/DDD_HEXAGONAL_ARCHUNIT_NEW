package com.satellite.infrastructure.adapter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.satellite.domain.model.Orbit;
import com.satellite.infrastructure.adapter.in.rest.dto.LaunchSatelliteRequest;
import com.satellite.infrastructure.adapter.in.rest.dto.UpdateTelemetryRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Satellite Rest Controller Integration Test")
class SatelliteControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Should launch satellite and return 201 Created")
    void shouldLaunchSatellite() throws Exception {
        LaunchSatelliteRequest request = new LaunchSatelliteRequest(
                "Hubble-2", "NASA", 540.0, 28.5, Orbit.OrbitType.LEO);

        mockMvc.perform(post("/api/v1/satellites")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("Hubble-2"))
                .andExpect(jsonPath("$.owner").value("NASA"))
                .andExpect(jsonPath("$.status").value("ACTIVE"))
                .andExpect(jsonPath("$.orbitType").value("LEO"));
    }

    @Test
    @DisplayName("Should update telemetry and return 200 OK")
    void shouldUpdateTelemetry() throws Exception {
        // 1. Launch satellite
        LaunchSatelliteRequest launchReq = new LaunchSatelliteRequest(
                "JWST-2", "ESA", 35786.0, 5.0, Orbit.OrbitType.GEO);

        String responseJson = mockMvc.perform(post("/api/v1/satellites")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(launchReq)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        String satelliteId = objectMapper.readTree(responseJson).get("id").asText();

        // 2. Update telemetry
        UpdateTelemetryRequest telemetryReq = new UpdateTelemetryRequest(88.5, -75.0, 22.0);

        mockMvc.perform(put("/api/v1/satellites/" + satelliteId + "/telemetry")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(telemetryReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.batteryPercentage").value(88.5))
                .andExpect(jsonPath("$.signalStrengthDbm").value(-75.0))
                .andExpect(jsonPath("$.isAnomalous").value(false));
    }

    @Test
    @DisplayName("Should detect anomaly when telemetry threshold is breached")
    void shouldDetectAnomaly() throws Exception {
        LaunchSatelliteRequest launchReq = new LaunchSatelliteRequest(
                "Sentinel-1", "Copernicus", 693.0, 98.18, Orbit.OrbitType.LEO);

        String responseJson = mockMvc.perform(post("/api/v1/satellites")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(launchReq)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        String satelliteId = objectMapper.readTree(responseJson).get("id").asText();

        // Low battery (10% < 15% threshold) triggers ANOMALY status
        UpdateTelemetryRequest lowBatteryReq = new UpdateTelemetryRequest(10.0, -70.0, 20.0);

        mockMvc.perform(put("/api/v1/satellites/" + satelliteId + "/telemetry")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(lowBatteryReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ANOMALY"))
                .andExpect(jsonPath("$.isAnomalous").value(true));
    }

    @Test
    @DisplayName("Should return all satellites")
    void shouldReturnAllSatellites() throws Exception {
        mockMvc.perform(get("/api/v1/satellites"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(0))));
    }
}
