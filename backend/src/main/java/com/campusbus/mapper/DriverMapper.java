package com.campusbus.mapper;

import com.campusbus.dto.request.DriverCreateRequest;
import com.campusbus.dto.request.DriverUpdateRequest;
import com.campusbus.dto.response.DriverResponse;
import com.campusbus.entity.Bus;
import com.campusbus.entity.Driver;
import com.campusbus.entity.enums.DriverStatus;

public class DriverMapper {

    private DriverMapper() {
        // Utility class — prevent instantiation
    }

    public static Driver toEntity(DriverCreateRequest request) {
        Driver driver = new Driver();
        driver.setName(request.getName());
        driver.setLicenseNumber(request.getLicenseNumber());
        driver.setPhoneNumber(request.getPhoneNumber());
        driver.setStatus(DriverStatus.valueOf(request.getStatus().toUpperCase()));
        return driver;
    }

    public static void updateEntity(Driver driver, DriverUpdateRequest request) {
        driver.setName(request.getName());
        driver.setLicenseNumber(request.getLicenseNumber());
        driver.setPhoneNumber(request.getPhoneNumber());
        driver.setStatus(DriverStatus.valueOf(request.getStatus().toUpperCase()));
    }

    public static DriverResponse toResponse(Driver driver) {
        Bus bus = driver.getAssignedBus();

        return DriverResponse.builder()
                .id(driver.getId())
                .name(driver.getName())
                .licenseNumber(driver.getLicenseNumber())
                .phoneNumber(driver.getPhoneNumber())
                .status(driver.getStatus().name())
                .assignedBusId(bus != null ? bus.getId() : null)
                .assignedBusNumber(bus != null ? bus.getBusNumber() : null)
                .createdAt(driver.getCreatedAt())
                .updatedAt(driver.getUpdatedAt())
                .build();
    }
}
