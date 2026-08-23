package com.satellite.adapter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.satellite.domain.model.Orbit;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration test — boots the full Spring context with H2 in-memory DB.
 * Tests the full vertical slice: REST → Application → Domain → Persistence.
 *
 * <p>Each test is wrapped in a transaction that rolls back at the end,
 * keeping tests isolated.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@DisplayName("Satellite REST API Integration")
class SatelliteControllerIntegrationTest {

    @Autowired MockMvc    mockMvc;
    @Autowired ObjectMapper objectMapper;

    // ── Helpers ───────────────────────────────────────────────────────────────

    private String launchRequest(String name, String owner,
                                 double altitude, double inclination,
                                 Orbit.OrbitType orbitType) throws Exception {
        return objectMapper.writeValueAsString(Map.of(
                "name", name,
                "owner", owner,
                "orbit", Map.of(
                        "altitudeKm", altitude,
                        "inclinationDegrees", inclination,
                        "orbitType", orbitType.name()
                )
        ));
    }

    private UUID launchSatellite(String name) throws Exception {
        String body = launchRequest(name, "TestOrg", 550.0, 53.0, Orbit.OrbitType.LEO);
        MvcResult result = mockMvc.perform(post("/api/v1/satellites")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andReturn();

        Map<?, ?> response = objectMapper.readValue(
                result.getResponse().getContentAsString(), Map.class);
        return UUID.fromString((String) response.get("id"));
    }

    // ── POST /api/v1/satellites ───────────────────────────────────────────────

    @Test
    @DisplayName("POST /satellites should return 201 and ACTIVE satellite")
    void launchSatellite_shouldReturn201() throws Exception {
        String body = launchRequest("Hubble-2", "NASA", 550.0, 28.5, Orbit.OrbitType.LEO);

        mockMvc.perform(post("/api/v1/satellites")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name",   is("Hubble-2")))
                .andExpect(jsonPath("$.owner",  is("NASA")))
                .andExpect(jsonPath("$.status", is("ACTIVE")))
                .andExpect(jsonPath("$.id",     notNullValue()))
                .andExpect(jsonPath("$.orbit.orbitType",  is("LEO")))
                .andExpect(jsonPath("$.orbit.altitudeKm", is(550.0)));
    }

    @Test
    @DisplayName("POST /satellites should return 400 for missing name")
    void launchSatellite_shouldReturn400ForMissingName() throws Exception {
        String body = objectMapper.writeValueAsString(Map.of(
                "owner", "NASA",
                "orbit", Map.of("altitudeKm", 550.0, "inclinationDegrees", 28.5,
                                "orbitType", "LEO")
        ));

        mockMvc.perform(post("/api/v1/satellites")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /satellites should return 422 for invalid orbit type")
    void launchSatellite_shouldReturn422ForInvalidOrbit() throws Exception {
        // LEO altitude of 5000km violates domain rule
        String body = objectMapper.writeValueAsString(Map.of(
                "name", "BadSat",
                "owner", "ESA",
                "orbit", Map.of("altitudeKm", 5000.0, "inclinationDegrees", 0.0,
                                "orbitType", "LEO")
        ));

        mockMvc.perform(post("/api/v1/satellites")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.code", is("DOMAIN_ERROR")));
    }

    // ── GET /api/v1/satellites ────────────────────────────────────────────────

    @Test
    @DisplayName("GET /satellites should return list of all satellites")
    void getAllSatellites_shouldReturnList() throws Exception {
        launchSatellite("Sat-Alpha");
        launchSatellite("Sat-Beta");

        mockMvc.perform(get("/api/v1/satellites"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(2))));
    }

    // ── GET /api/v1/satellites/{id} ───────────────────────────────────────────

    @Test
    @DisplayName("GET /satellites/{id} should return correct satellite")
    void getSatelliteById_shouldReturnSatellite() throws Exception {
        UUID id = launchSatellite("Galileo-3");

        mockMvc.perform(get("/api/v1/satellites/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id",   is(id.toString())))
                .andExpect(jsonPath("$.name", is("Galileo-3")));
    }

    @Test
    @DisplayName("GET /satellites/{id} should return 422 for unknown ID")
    void getSatelliteById_shouldReturn422ForUnknownId() throws Exception {
        mockMvc.perform(get("/api/v1/satellites/" + UUID.randomUUID()))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.code", is("DOMAIN_ERROR")));
    }

    // ── PATCH /api/v1/satellites/{id}/telemetry ───────────────────────────────

    @Test
    @DisplayName("PATCH /satellites/{id}/telemetry should update and detect anomaly")
    void updateTelemetry_shouldDetectAnomaly() throws Exception {
        UUID id = launchSatellite("AnomalySat-1");

        String telemetryBody = objectMapper.writeValueAsString(Map.of(
                "batteryPercentage",  5.0,   // critical!
                "signalStrengthDbm", -90.0,
                "temperatureCelsius", 25.0
        ));

        mockMvc.perform(patch("/api/v1/satellites/" + id + "/telemetry")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(telemetryBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status",                       is("ANOMALY")))
                .andExpect(jsonPath("$.telemetry.anomalous",          is(true)))
                .andExpect(jsonPath("$.telemetry.batteryPercentage",  is(5.0)));
    }

    @Test
    @DisplayName("PATCH /satellites/{id}/telemetry with healthy readings should stay ACTIVE")
    void updateTelemetry_healthyReadings_shouldRemainActive() throws Exception {
        UUID id = launchSatellite("HealthySat-1");

        String telemetryBody = objectMapper.writeValueAsString(Map.of(
                "batteryPercentage",  85.0,
                "signalStrengthDbm", -75.0,
                "temperatureCelsius", 22.0
        ));

        mockMvc.perform(patch("/api/v1/satellites/" + id + "/telemetry")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(telemetryBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status",              is("ACTIVE")))
                .andExpect(jsonPath("$.telemetry.anomalous", is(false)))
                .andExpect(jsonPath("$.telemetry.anomalySummary", is("NOMINAL")));
    }
}
