package com.campusbus.controller;

import com.campusbus.dto.response.DriverResponse;
import com.campusbus.exception.ResourceNotFoundException;
import com.campusbus.service.DriverService;
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

@SuppressWarnings("removal")
@WebMvcTest(DriverController.class)
class DriverControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DriverService driverService;

    @Test
    void createDriver_shouldReturn201WhenValid() throws Exception {
        DriverResponse response = DriverResponse.builder()
                .id(1L)
                .name("Test Driver")
                .licenseNumber("TN0120260001234")
                .phoneNumber("9876543210")
                .status("ACTIVE")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(driverService.createDriver(any())).thenReturn(response);

        String requestBody = """
                {
                    "name": "Test Driver",
                    "licenseNumber": "TN0120260001234",
                    "phoneNumber": "9876543210",
                    "status": "ACTIVE"
                }
                """;

        mockMvc.perform(post("/api/drivers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Test Driver"))
                .andExpect(jsonPath("$.licenseNumber").value("TN0120260001234"));
    }

    @Test
    void createDriver_shouldReturn400WhenNameBlank() throws Exception {
        String requestBody = """
                {
                    "name": "",
                    "licenseNumber": "TN0120260001234",
                    "phoneNumber": "9876543210",
                    "status": "ACTIVE"
                }
                """;

        mockMvc.perform(post("/api/drivers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void createDriver_shouldReturn400WhenPhoneInvalid() throws Exception {
        String requestBody = """
                {
                    "name": "Test Driver",
                    "licenseNumber": "TN0120260001234",
                    "phoneNumber": "abc",
                    "status": "ACTIVE"
                }
                """;

        mockMvc.perform(post("/api/drivers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void getAllDrivers_shouldReturn200() throws Exception {
        DriverResponse response = DriverResponse.builder()
                .id(1L)
                .name("Test Driver")
                .licenseNumber("TN0120260001234")
                .phoneNumber("9876543210")
                .status("ACTIVE")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(driverService.getAllDrivers()).thenReturn(List.of(response));

        mockMvc.perform(get("/api/drivers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Test Driver"));
    }

    @Test
    void getDriverById_shouldReturn404WhenNotFound() throws Exception {
        when(driverService.getDriverById(99L))
                .thenThrow(new ResourceNotFoundException("Driver not found with id: 99"));

        mockMvc.perform(get("/api/drivers/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Driver not found with id: 99"));
    }
}
