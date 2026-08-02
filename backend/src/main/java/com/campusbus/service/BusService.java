package com.campusbus.service;

import com.campusbus.dto.request.BusCreateRequest;
import com.campusbus.dto.request.BusUpdateRequest;
import com.campusbus.dto.response.BusResponse;

import java.util.List;

public interface BusService {

    BusResponse createBus(BusCreateRequest request);

    List<BusResponse> getAllBuses();

    BusResponse getBusById(Long id);

    BusResponse updateBus(Long id, BusUpdateRequest request);

    void deleteBus(Long id);
}
