package com.campusbus.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DriverResponse {
    private Long id;
    private String name;
    private String licenseNumber;
    private String phoneNumber;
    private String status;
    private Long assignedBusId;
    private String assignedBusNumber;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
