package com.pfa.service_sinistre.Repository;

import com.pfa.service_sinistre.entity.Incident;
import com.pfa.service_sinistre.entity.IncidentStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IncidentRepository extends JpaRepository<Incident, Long> {

    List<Incident> findByVehicleId(Long vehicleId);

    List<Incident> findByLineId(Long lineId);

    List<Incident> findByStatus(IncidentStatus status);

    List<Incident> findByReportedBy(String reportedBy);

    // recent (sans query)
    List<Incident> findAllByOrderByReportedAtDesc(Pageable pageable);
}
