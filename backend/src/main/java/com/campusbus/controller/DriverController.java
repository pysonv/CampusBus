package com.campusbus.controller;

import com.campusbus.dto.request.DriverBusAssignmentRequest;
import com.campusbus.dto.request.DriverCreateRequest;
import com.campusbus.dto.request.DriverUpdateRequest;
import com.campusbus.dto.response.DriverResponse;
import com.campusbus.service.DriverService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/drivers")
public class DriverController {

    private final DriverService driverService;

    public DriverController(DriverService driverService) {
        this.driverService = driverService;
    }

    @PostMapping
    public ResponseEntity<DriverResponse> createDriver(@Valid @RequestBody DriverCreateRequest request) {
        DriverResponse response = driverService.createDriver(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<DriverResponse>> getAllDrivers() {
        List<DriverResponse> drivers = driverService.getAllDrivers();
        return ResponseEntity.ok(drivers);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DriverResponse> getDriverById(@PathVariable Long id) {
        DriverResponse response = driverService.getDriverById(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DriverResponse> updateDriver(@PathVariable Long id,
                                                        @Valid @RequestBody DriverUpdateRequest request) {
        DriverResponse response = driverService.updateDriver(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDriver(@PathVariable Long id) {
        driverService.deleteDriver(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{driverId}/bus")
    public ResponseEntity<DriverResponse> assignBus(@PathVariable Long driverId,
                                                     @Valid @RequestBody DriverBusAssignmentRequest request) {
        DriverResponse response = driverService.assignBus(driverId, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{driverId}/bus")
    public ResponseEntity<DriverResponse> unassignBus(@PathVariable Long driverId) {
        DriverResponse response = driverService.unassignBus(driverId);
        return ResponseEntity.ok(response);
    }
}
