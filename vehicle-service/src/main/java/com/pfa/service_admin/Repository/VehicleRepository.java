package com.pfa.service_admin.Repository;

import com.pfa.service_admin.Entity.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

    Optional<Vehicle> findByCode(String code);

    List<Vehicle> findByTripStatusIgnoreCase(String tripStatus);
    List<Vehicle> findByLineId(Long lineId);
}
