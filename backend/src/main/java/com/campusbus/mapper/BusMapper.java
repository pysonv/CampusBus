package com.campusbus.mapper;

import com.campusbus.dto.request.BusCreateRequest;
import com.campusbus.dto.request.BusUpdateRequest;
import com.campusbus.dto.response.BusResponse;
import com.campusbus.entity.Bus;
import com.campusbus.entity.enums.BusStatus;

public class BusMapper {

    private BusMapper() {
        // Utility class — prevent instantiation
    }

    public static Bus toEntity(BusCreateRequest request) {
        Bus bus = new Bus();
        bus.setBusNumber(request.getBusNumber());
        bus.setRegistrationNumber(request.getRegistrationNumber());
        bus.setCapacity(request.getCapacity());
        bus.setStatus(BusStatus.valueOf(request.getStatus().toUpperCase()));
        return bus;
    }

    public static void updateEntity(Bus bus, BusUpdateRequest request) {
        bus.setBusNumber(request.getBusNumber());
        bus.setRegistrationNumber(request.getRegistrationNumber());
        bus.setCapacity(request.getCapacity());
        bus.setStatus(BusStatus.valueOf(request.getStatus().toUpperCase()));
    }

    public static BusResponse toResponse(Bus bus) {
        return BusResponse.builder()
                .id(bus.getId())
                .busNumber(bus.getBusNumber())
                .registrationNumber(bus.getRegistrationNumber())
                .capacity(bus.getCapacity())
                .status(bus.getStatus().name())
                .createdAt(bus.getCreatedAt())
                .updatedAt(bus.getUpdatedAt())
                .build();
    }
}
