package com.campusbus.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DriverBusAssignmentRequest {

    @NotNull(message = "Bus ID is required")
    private Long busId;
}
