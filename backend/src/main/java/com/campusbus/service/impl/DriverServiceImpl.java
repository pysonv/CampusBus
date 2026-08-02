package com.campusbus.service.impl;

import com.campusbus.dto.request.DriverBusAssignmentRequest;
import com.campusbus.dto.request.DriverCreateRequest;
import com.campusbus.dto.request.DriverUpdateRequest;
import com.campusbus.dto.response.DriverResponse;
import com.campusbus.entity.Bus;
import com.campusbus.entity.Driver;
import com.campusbus.entity.enums.BusStatus;
import com.campusbus.exception.BusinessRuleException;
import com.campusbus.exception.DuplicateResourceException;
import com.campusbus.exception.ResourceNotFoundException;
import com.campusbus.mapper.DriverMapper;
import com.campusbus.repository.BusRepository;
import com.campusbus.repository.DriverRepository;
import com.campusbus.service.DriverService;
import com.campusbus.util.LicenseNumberUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DriverServiceImpl implements DriverService {

    private final DriverRepository driverRepository;
    private final BusRepository busRepository;

    public DriverServiceImpl(DriverRepository driverRepository, BusRepository busRepository) {
        this.driverRepository = driverRepository;
        this.busRepository = busRepository;
    }

    @Override
    @Transactional
    public DriverResponse createDriver(DriverCreateRequest request) {
        String normalizedLicense = LicenseNumberUtil.normalize(request.getLicenseNumber());
        request.setLicenseNumber(normalizedLicense);
        request.setPhoneNumber(request.getPhoneNumber().trim());

        if (driverRepository.existsByLicenseNumber(normalizedLicense)) {
            throw new DuplicateResourceException(
                    "Driver with license number '" + normalizedLicense + "' already exists");
        }

        Driver driver = DriverMapper.toEntity(request);
        Driver savedDriver = driverRepository.save(driver);
        return DriverMapper.toResponse(savedDriver);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DriverResponse> getAllDrivers() {
        return driverRepository.findAll()
                .stream()
                .map(DriverMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public DriverResponse getDriverById(Long id) {
        Driver driver = driverRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Driver not found with id: " + id));
        return DriverMapper.toResponse(driver);
    }

    @Override
    @Transactional
    public DriverResponse updateDriver(Long id, DriverUpdateRequest request) {
        Driver existingDriver = driverRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Driver not found with id: " + id));

        String normalizedLicense = LicenseNumberUtil.normalize(request.getLicenseNumber());
        request.setLicenseNumber(normalizedLicense);
        request.setPhoneNumber(request.getPhoneNumber().trim());

        if (driverRepository.existsByLicenseNumberAndIdNot(normalizedLicense, id)) {
            throw new DuplicateResourceException(
                    "Driver with license number '" + normalizedLicense + "' already exists");
        }

        DriverMapper.updateEntity(existingDriver, request);
        Driver updatedDriver = driverRepository.save(existingDriver);
        return DriverMapper.toResponse(updatedDriver);
    }

    @Override
    @Transactional
    public void deleteDriver(Long id) {
        Driver driver = driverRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Driver not found with id: " + id));
        driverRepository.delete(driver);
    }

    @Override
    @Transactional
    public DriverResponse assignBus(Long driverId, DriverBusAssignmentRequest request) {
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new ResourceNotFoundException("Driver not found with id: " + driverId));

        Bus bus = busRepository.findById(request.getBusId())
                .orElseThrow(() -> new ResourceNotFoundException("Bus not found with id: " + request.getBusId()));

        // Only allow assignment to ACTIVE buses
        if (bus.getStatus() != BusStatus.ACTIVE) {
            throw new BusinessRuleException(
                    "Cannot assign driver to bus '" + bus.getBusNumber()
                            + "' because its status is " + bus.getStatus());
        }

        // Check if another driver is already assigned to this bus
        if (driverRepository.existsByAssignedBusIdAndIdNot(bus.getId(), driverId)) {
            throw new BusinessRuleException(
                    "Bus '" + bus.getBusNumber() + "' is already assigned to another driver");
        }

        driver.setAssignedBus(bus);
        Driver updatedDriver = driverRepository.save(driver);
        return DriverMapper.toResponse(updatedDriver);
    }

    @Override
    @Transactional
    public DriverResponse unassignBus(Long driverId) {
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new ResourceNotFoundException("Driver not found with id: " + driverId));

        driver.setAssignedBus(null);
        Driver updatedDriver = driverRepository.save(driver);
        return DriverMapper.toResponse(updatedDriver);
    }
}
