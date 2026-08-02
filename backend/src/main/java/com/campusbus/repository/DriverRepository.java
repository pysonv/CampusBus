package com.campusbus.repository;

import com.campusbus.entity.Driver;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DriverRepository extends JpaRepository<Driver, Long> {

    boolean existsByLicenseNumber(String licenseNumber);

    Optional<Driver> findByLicenseNumber(String licenseNumber);

    boolean existsByLicenseNumberAndIdNot(String licenseNumber, Long id);

    boolean existsByAssignedBusIdAndIdNot(Long busId, Long driverId);

    boolean existsByAssignedBusId(Long busId);
}
