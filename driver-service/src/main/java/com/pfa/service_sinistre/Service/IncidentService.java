package com.pfa.service_sinistre.Service;

import com.pfa.service_sinistre.entity.Incident;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface IncidentService {

    Incident reportIncident(Incident incident);

    Incident updateStatus(Long id, String newStatus);

    List<Incident> getIncidentsForVehicle(Long vehicleId);

    List<Incident> getOpenIncidents();

    Incident getById(Long id);

    List<Incident> searchIncidents(
            String status,
            String severity,
            Long vehicleId,
            String reportedBy,
            Long lineId,
            LocalDateTime fromDate,
            LocalDateTime toDate
    );

    List<Incident> getRecentIncidents(int limit);

    List<Incident> getIncidentsForReporter(String reportedBy);

    List<Incident> getIncidentsForLine(Long lineId);

    Map<String, Long> getIncidentCountByStatus();

    void deleteIncident(Long id);
}
