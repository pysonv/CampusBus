package com.campusbus.controller;

import com.campusbus.dto.response.BusResponse;
import com.campusbus.exception.ResourceNotFoundException;
import com.campusbus.service.BusService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BusController.class)
class BusControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @SuppressWarnings("removal")
    @MockBean
    private BusService busService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createBus_shouldReturn201WhenValid() throws Exception {
        BusResponse response = BusResponse.builder()
                .id(1L)
                .busNumber("BUS-001")
                .registrationNumber("TN01AB1234")
                .capacity(50)
                .status("ACTIVE")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(busService.createBus(any())).thenReturn(response);

        String requestBody = """
                {
                    "busNumber": "BUS-001",
                    "registrationNumber": "TN01AB1234",
                    "capacity": 50,
                    "status": "ACTIVE"
                }
                """;

        mockMvc.perform(post("/api/buses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.busNumber").value("BUS-001"))
                .andExpect(jsonPath("$.registrationNumber").value("TN01AB1234"))
                .andExpect(jsonPath("$.capacity").value(50))
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    void createBus_shouldReturn400WhenCapacityIsZero() throws Exception {
        String requestBody = """
                {
                    "busNumber": "BUS-002",
                    "registrationNumber": "TN02CD5678",
                    "capacity": 0,
                    "status": "ACTIVE"
                }
                """;

        mockMvc.perform(post("/api/buses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("must be greater than 0")));
    }

    @Test
    void createBus_shouldReturn400WhenBusNumberBlank() throws Exception {
        String requestBody = """
                {
                    "busNumber": "",
                    "registrationNumber": "TN02CD5678",
                    "capacity": 50,
                    "status": "ACTIVE"
                }
                """;

        mockMvc.perform(post("/api/buses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void getAllBuses_shouldReturn200() throws Exception {
        BusResponse response = BusResponse.builder()
                .id(1L)
                .busNumber("BUS-001")
                .registrationNumber("TN01AB1234")
                .capacity(50)
                .status("ACTIVE")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(busService.getAllBuses()).thenReturn(List.of(response));

        mockMvc.perform(get("/api/buses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].busNumber").value("BUS-001"));
    }

    @Test
    void getBusById_shouldReturn404WhenNotFound() throws Exception {
        when(busService.getBusById(99L)).thenThrow(new ResourceNotFoundException("Bus not found with id: 99"));

        mockMvc.perform(get("/api/buses/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Bus not found with id: 99"));
    }
}
