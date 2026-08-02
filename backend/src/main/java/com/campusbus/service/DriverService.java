package com.campusbus.service;

import com.campusbus.dto.request.DriverBusAssignmentRequest;
import com.campusbus.dto.request.DriverCreateRequest;
import com.campusbus.dto.request.DriverUpdateRequest;
import com.campusbus.dto.response.DriverResponse;

import java.util.List;

public interface DriverService {

    DriverResponse createDriver(DriverCreateRequest request);

    List<DriverResponse> getAllDrivers();

    DriverResponse getDriverById(Long id);

    DriverResponse updateDriver(Long id, DriverUpdateRequest request);

    void deleteDriver(Long id);

    DriverResponse assignBus(Long driverId, DriverBusAssignmentRequest request);

    DriverResponse unassignBus(Long driverId);
}
