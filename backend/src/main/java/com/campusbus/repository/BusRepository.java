package com.campusbus.repository;

import com.campusbus.entity.Bus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BusRepository extends JpaRepository<Bus, Long> {

    boolean existsByBusNumber(String busNumber);

    boolean existsByRegistrationNumber(String registrationNumber);

    Optional<Bus> findByRegistrationNumber(String registrationNumber);

    boolean existsByBusNumberAndIdNot(String busNumber, Long id);

    boolean existsByRegistrationNumberAndIdNot(String registrationNumber, Long id);
}
