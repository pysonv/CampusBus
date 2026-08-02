package com.campusbus.service.impl;

import com.campusbus.dto.request.BusCreateRequest;
import com.campusbus.dto.request.BusUpdateRequest;
import com.campusbus.dto.response.BusResponse;
import com.campusbus.entity.Bus;
import com.campusbus.exception.DuplicateResourceException;
import com.campusbus.exception.ResourceNotFoundException;
import com.campusbus.mapper.BusMapper;
import com.campusbus.repository.BusRepository;
import com.campusbus.service.BusService;
import com.campusbus.util.RegistrationNumberUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BusServiceImpl implements BusService {

    private final BusRepository busRepository;

    public BusServiceImpl(BusRepository busRepository) {
        this.busRepository = busRepository;
    }

    @Override
    @Transactional
    public BusResponse createBus(BusCreateRequest request) {
        // Normalize registration number before any duplicate check
        String normalizedReg = RegistrationNumberUtil.normalize(request.getRegistrationNumber());
        request.setRegistrationNumber(normalizedReg);

        // Check for duplicate bus number
        if (busRepository.existsByBusNumber(request.getBusNumber())) {
            throw new DuplicateResourceException(
                    "Bus with bus number '" + request.getBusNumber() + "' already exists");
        }

        // Check for duplicate registration number
        if (busRepository.existsByRegistrationNumber(normalizedReg)) {
            throw new DuplicateResourceException(
                    "Bus with registration number '" + normalizedReg + "' already exists");
        }

        Bus bus = BusMapper.toEntity(request);
        Bus savedBus = busRepository.save(bus);
        return BusMapper.toResponse(savedBus);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BusResponse> getAllBuses() {
        return busRepository.findAll()
                .stream()
                .map(BusMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public BusResponse getBusById(Long id) {
        Bus bus = busRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bus not found with id: " + id));
        return BusMapper.toResponse(bus);
    }

    @Override
    @Transactional
    public BusResponse updateBus(Long id, BusUpdateRequest request) {
        Bus existingBus = busRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bus not found with id: " + id));

        // Normalize registration number
        String normalizedReg = RegistrationNumberUtil.normalize(request.getRegistrationNumber());
        request.setRegistrationNumber(normalizedReg);

        // Check duplicate bus number (exclude current bus)
        if (busRepository.existsByBusNumberAndIdNot(request.getBusNumber(), id)) {
            throw new DuplicateResourceException(
                    "Bus with bus number '" + request.getBusNumber() + "' already exists");
        }

        // Check duplicate registration number (exclude current bus)
        if (busRepository.existsByRegistrationNumberAndIdNot(normalizedReg, id)) {
            throw new DuplicateResourceException(
                    "Bus with registration number '" + normalizedReg + "' already exists");
        }

        BusMapper.updateEntity(existingBus, request);
        Bus updatedBus = busRepository.save(existingBus);
        return BusMapper.toResponse(updatedBus);
    }

    @Override
    @Transactional
    public void deleteBus(Long id) {
        Bus bus = busRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bus not found with id: " + id));
        busRepository.delete(bus);
    }
}
