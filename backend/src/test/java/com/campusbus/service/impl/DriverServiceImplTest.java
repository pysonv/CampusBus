package com.campusbus.service.impl;

import com.campusbus.dto.request.DriverBusAssignmentRequest;
import com.campusbus.dto.request.DriverCreateRequest;
import com.campusbus.dto.request.DriverUpdateRequest;
import com.campusbus.dto.response.DriverResponse;
import com.campusbus.entity.Bus;
import com.campusbus.entity.Driver;
import com.campusbus.entity.enums.BusStatus;
import com.campusbus.entity.enums.DriverStatus;
import com.campusbus.exception.BusinessRuleException;
import com.campusbus.exception.DuplicateResourceException;
import com.campusbus.exception.ResourceNotFoundException;
import com.campusbus.repository.BusRepository;
import com.campusbus.repository.DriverRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DriverServiceImplTest {

    @Mock
    private DriverRepository driverRepository;

    @Mock
    private BusRepository busRepository;

    @InjectMocks
    private DriverServiceImpl driverService;

    private Driver sampleDriver;
    private Bus sampleBus;

    @BeforeEach
    void setUp() {
        sampleBus = new Bus();
        sampleBus.setId(1L);
        sampleBus.setBusNumber("BUS-001");
        sampleBus.setRegistrationNumber("TN01AB1234");
        sampleBus.setCapacity(50);
        sampleBus.setStatus(BusStatus.ACTIVE);
        sampleBus.setCreatedAt(LocalDateTime.now());
        sampleBus.setUpdatedAt(LocalDateTime.now());

        sampleDriver = new Driver();
        sampleDriver.setId(1L);
        sampleDriver.setName("Test Driver");
        sampleDriver.setLicenseNumber("TN0120260001234");
        sampleDriver.setPhoneNumber("9876543210");
        sampleDriver.setStatus(DriverStatus.ACTIVE);
        sampleDriver.setAssignedBus(null);
        sampleDriver.setCreatedAt(LocalDateTime.now());
        sampleDriver.setUpdatedAt(LocalDateTime.now());
    }

    // --- CREATE ---

    @Test
    void createDriver_shouldCreateSuccessfully() {
        DriverCreateRequest request = new DriverCreateRequest(
                "Test Driver", "tn 01 2026 0001234", "9876543210", "ACTIVE");

        when(driverRepository.existsByLicenseNumber("TN0120260001234")).thenReturn(false);
        when(driverRepository.save(any(Driver.class))).thenReturn(sampleDriver);

        DriverResponse response = driverService.createDriver(request);

        assertNotNull(response);
        assertEquals("Test Driver", response.getName());
        assertEquals("TN0120260001234", response.getLicenseNumber());
        assertNull(response.getAssignedBusId());
        verify(driverRepository).save(any(Driver.class));
    }

    @Test
    void createDriver_shouldNormalizeLicenseNumber() {
        DriverCreateRequest request = new DriverCreateRequest(
                "Test Driver", "tn-01-2026-0001234", "9876543210", "ACTIVE");

        when(driverRepository.existsByLicenseNumber("TN0120260001234")).thenReturn(false);
        when(driverRepository.save(any(Driver.class))).thenReturn(sampleDriver);

        DriverResponse response = driverService.createDriver(request);

        assertEquals("TN0120260001234", response.getLicenseNumber());
    }

    @Test
    void createDriver_shouldRejectDuplicateLicenseNumber() {
        DriverCreateRequest request = new DriverCreateRequest(
                "Test Driver", "TN0120260001234", "9876543210", "ACTIVE");

        when(driverRepository.existsByLicenseNumber("TN0120260001234")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> driverService.createDriver(request));
        verify(driverRepository, never()).save(any());
    }

    // --- GET ---

    @Test
    void getDriverById_shouldReturnDriverWhenFound() {
        when(driverRepository.findById(1L)).thenReturn(Optional.of(sampleDriver));

        DriverResponse response = driverService.getDriverById(1L);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Test Driver", response.getName());
    }

    @Test
    void getDriverById_shouldThrowWhenNotFound() {
        when(driverRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> driverService.getDriverById(99L));
    }

    @Test
    void getAllDrivers_shouldReturnList() {
        when(driverRepository.findAll()).thenReturn(List.of(sampleDriver));

        List<DriverResponse> drivers = driverService.getAllDrivers();

        assertEquals(1, drivers.size());
        assertEquals("Test Driver", drivers.get(0).getName());
    }

    // --- UPDATE ---

    @Test
    void updateDriver_shouldUpdateSuccessfully() {
        DriverUpdateRequest request = new DriverUpdateRequest(
                "Updated Driver", "TN0120260001234", "9876543210", "INACTIVE");

        when(driverRepository.findById(1L)).thenReturn(Optional.of(sampleDriver));
        when(driverRepository.existsByLicenseNumberAndIdNot("TN0120260001234", 1L)).thenReturn(false);
        when(driverRepository.save(any(Driver.class))).thenReturn(sampleDriver);

        DriverResponse response = driverService.updateDriver(1L, request);

        assertNotNull(response);
        verify(driverRepository).save(any(Driver.class));
    }

    // --- DELETE ---

    @Test
    void deleteDriver_shouldDeleteWhenFound() {
        when(driverRepository.findById(1L)).thenReturn(Optional.of(sampleDriver));

        driverService.deleteDriver(1L);

        verify(driverRepository).delete(sampleDriver);
    }

    @Test
    void deleteDriver_shouldThrowWhenNotFound() {
        when(driverRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> driverService.deleteDriver(99L));
        verify(driverRepository, never()).delete(any());
    }

    // --- ASSIGN BUS ---

    @Test
    void assignBus_shouldAssignActiveBusSuccessfully() {
        DriverBusAssignmentRequest request = new DriverBusAssignmentRequest(1L);

        when(driverRepository.findById(1L)).thenReturn(Optional.of(sampleDriver));
        when(busRepository.findById(1L)).thenReturn(Optional.of(sampleBus));
        when(driverRepository.existsByAssignedBusIdAndIdNot(1L, 1L)).thenReturn(false);

        Driver driverWithBus = new Driver();
        driverWithBus.setId(1L);
        driverWithBus.setName("Test Driver");
        driverWithBus.setLicenseNumber("TN0120260001234");
        driverWithBus.setPhoneNumber("9876543210");
        driverWithBus.setStatus(DriverStatus.ACTIVE);
        driverWithBus.setAssignedBus(sampleBus);
        driverWithBus.setCreatedAt(LocalDateTime.now());
        driverWithBus.setUpdatedAt(LocalDateTime.now());

        when(driverRepository.save(any(Driver.class))).thenReturn(driverWithBus);

        DriverResponse response = driverService.assignBus(1L, request);

        assertNotNull(response);
        assertEquals(1L, response.getAssignedBusId());
        assertEquals("BUS-001", response.getAssignedBusNumber());
    }

    @Test
    void assignBus_shouldThrowWhenBusNotFound() {
        DriverBusAssignmentRequest request = new DriverBusAssignmentRequest(99L);

        when(driverRepository.findById(1L)).thenReturn(Optional.of(sampleDriver));
        when(busRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> driverService.assignBus(1L, request));
    }

    @Test
    void assignBus_shouldRejectWhenBusAlreadyAssigned() {
        DriverBusAssignmentRequest request = new DriverBusAssignmentRequest(1L);

        when(driverRepository.findById(1L)).thenReturn(Optional.of(sampleDriver));
        when(busRepository.findById(1L)).thenReturn(Optional.of(sampleBus));
        when(driverRepository.existsByAssignedBusIdAndIdNot(1L, 1L)).thenReturn(true);

        assertThrows(BusinessRuleException.class, () -> driverService.assignBus(1L, request));
    }

    @Test
    void assignBus_shouldRejectInactiveBus() {
        sampleBus.setStatus(BusStatus.INACTIVE);
        DriverBusAssignmentRequest request = new DriverBusAssignmentRequest(1L);

        when(driverRepository.findById(1L)).thenReturn(Optional.of(sampleDriver));
        when(busRepository.findById(1L)).thenReturn(Optional.of(sampleBus));

        assertThrows(BusinessRuleException.class, () -> driverService.assignBus(1L, request));
    }

    @Test
    void assignBus_shouldRejectMaintenanceBus() {
        sampleBus.setStatus(BusStatus.MAINTENANCE);
        DriverBusAssignmentRequest request = new DriverBusAssignmentRequest(1L);

        when(driverRepository.findById(1L)).thenReturn(Optional.of(sampleDriver));
        when(busRepository.findById(1L)).thenReturn(Optional.of(sampleBus));

        assertThrows(BusinessRuleException.class, () -> driverService.assignBus(1L, request));
    }

    // --- UNASSIGN ---

    @Test
    void unassignBus_shouldClearAssignment() {
        sampleDriver.setAssignedBus(sampleBus);

        when(driverRepository.findById(1L)).thenReturn(Optional.of(sampleDriver));

        Driver unassignedDriver = new Driver();
        unassignedDriver.setId(1L);
        unassignedDriver.setName("Test Driver");
        unassignedDriver.setLicenseNumber("TN0120260001234");
        unassignedDriver.setPhoneNumber("9876543210");
        unassignedDriver.setStatus(DriverStatus.ACTIVE);
        unassignedDriver.setAssignedBus(null);
        unassignedDriver.setCreatedAt(LocalDateTime.now());
        unassignedDriver.setUpdatedAt(LocalDateTime.now());

        when(driverRepository.save(any(Driver.class))).thenReturn(unassignedDriver);

        DriverResponse response = driverService.unassignBus(1L);

        assertNull(response.getAssignedBusId());
        assertNull(response.getAssignedBusNumber());
    }
}
