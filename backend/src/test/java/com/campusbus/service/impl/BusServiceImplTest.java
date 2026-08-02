package com.campusbus.service.impl;

import com.campusbus.dto.request.BusCreateRequest;
import com.campusbus.dto.request.BusUpdateRequest;
import com.campusbus.dto.response.BusResponse;
import com.campusbus.entity.Bus;
import com.campusbus.entity.enums.BusStatus;
import com.campusbus.exception.DuplicateResourceException;
import com.campusbus.exception.ResourceNotFoundException;
import com.campusbus.repository.BusRepository;
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
class BusServiceImplTest {

    @Mock
    private BusRepository busRepository;

    @InjectMocks
    private BusServiceImpl busService;

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
    }

    @Test
    void createBus_shouldCreateSuccessfully() {
        BusCreateRequest request = new BusCreateRequest("BUS-001", "tn 01 ab 1234", 50, "ACTIVE");

        when(busRepository.existsByBusNumber("BUS-001")).thenReturn(false);
        when(busRepository.existsByRegistrationNumber("TN01AB1234")).thenReturn(false);
        when(busRepository.save(any(Bus.class))).thenReturn(sampleBus);

        BusResponse response = busService.createBus(request);

        assertNotNull(response);
        assertEquals("BUS-001", response.getBusNumber());
        assertEquals("TN01AB1234", response.getRegistrationNumber());
        assertEquals(50, response.getCapacity());
        assertEquals("ACTIVE", response.getStatus());
        verify(busRepository).save(any(Bus.class));
    }

    @Test
    void createBus_shouldNormalizeRegistrationNumber() {
        BusCreateRequest request = new BusCreateRequest("BUS-001", "tn-01-ab-1234", 50, "ACTIVE");

        when(busRepository.existsByBusNumber("BUS-001")).thenReturn(false);
        when(busRepository.existsByRegistrationNumber("TN01AB1234")).thenReturn(false);
        when(busRepository.save(any(Bus.class))).thenReturn(sampleBus);

        BusResponse response = busService.createBus(request);

        assertEquals("TN01AB1234", response.getRegistrationNumber());
    }

    @Test
    void createBus_shouldRejectDuplicateBusNumber() {
        BusCreateRequest request = new BusCreateRequest("BUS-001", "TN01AB1234", 50, "ACTIVE");

        when(busRepository.existsByBusNumber("BUS-001")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> busService.createBus(request));
        verify(busRepository, never()).save(any());
    }

    @Test
    void createBus_shouldRejectDuplicateRegistrationNumber() {
        BusCreateRequest request = new BusCreateRequest("BUS-002", "TN01AB1234", 50, "ACTIVE");

        when(busRepository.existsByBusNumber("BUS-002")).thenReturn(false);
        when(busRepository.existsByRegistrationNumber("TN01AB1234")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> busService.createBus(request));
        verify(busRepository, never()).save(any());
    }

    @Test
    void getBusById_shouldReturnBusWhenFound() {
        when(busRepository.findById(1L)).thenReturn(Optional.of(sampleBus));

        BusResponse response = busService.getBusById(1L);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("BUS-001", response.getBusNumber());
    }

    @Test
    void getBusById_shouldThrowWhenNotFound() {
        when(busRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> busService.getBusById(99L));
    }

    @Test
    void getAllBuses_shouldReturnList() {
        when(busRepository.findAll()).thenReturn(List.of(sampleBus));

        List<BusResponse> buses = busService.getAllBuses();

        assertEquals(1, buses.size());
        assertEquals("BUS-001", buses.get(0).getBusNumber());
    }

    @Test
    void updateBus_shouldUpdateSuccessfully() {
        BusUpdateRequest request = new BusUpdateRequest("BUS-001-UPDATED", "TN01AB1234", 60, "MAINTENANCE");

        when(busRepository.findById(1L)).thenReturn(Optional.of(sampleBus));
        when(busRepository.existsByBusNumberAndIdNot("BUS-001-UPDATED", 1L)).thenReturn(false);
        when(busRepository.existsByRegistrationNumberAndIdNot("TN01AB1234", 1L)).thenReturn(false);
        when(busRepository.save(any(Bus.class))).thenReturn(sampleBus);

        BusResponse response = busService.updateBus(1L, request);

        assertNotNull(response);
        verify(busRepository).save(any(Bus.class));
    }

    @Test
    void deleteBus_shouldDeleteWhenFound() {
        when(busRepository.findById(1L)).thenReturn(Optional.of(sampleBus));

        busService.deleteBus(1L);

        verify(busRepository).delete(sampleBus);
    }

    @Test
    void deleteBus_shouldThrowWhenNotFound() {
        when(busRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> busService.deleteBus(99L));
        verify(busRepository, never()).delete(any());
    }
}
