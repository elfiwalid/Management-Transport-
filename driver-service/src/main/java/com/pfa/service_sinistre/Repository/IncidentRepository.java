package com.pfa.service_sinistre.repository;

import com.pfa.service_sinistre.entity.Incident;
import com.pfa.service_sinistre.entity.IncidentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IncidentRepository extends JpaRepository<Incident, Long> {

    List<Incident> findByVehicleId(Long vehicleId);

    List<Incident> findByStatus(IncidentStatus status);
}
