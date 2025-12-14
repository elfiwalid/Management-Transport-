package com.pfa.service_sinistre.Service;

import com.pfa.service_sinistre.entity.Incident;
import com.pfa.service_sinistre.entity.IncidentSeverity;
import com.pfa.service_sinistre.entity.IncidentStatus;
import com.pfa.service_sinistre.Repository.IncidentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class IncidentServiceImpl implements IncidentService {

    private final IncidentRepository incidentRepository;

    @Override
    public Incident reportIncident(Incident incident) {
        incident.setId(null);
        incident.setStatus(IncidentStatus.OPEN);
        incident.setReportedAt(Instant.now());
        return incidentRepository.save(incident);
    }

    @Override
    public Incident updateStatus(Long id, String newStatus) {
        Incident inc = incidentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Incident non trouvé id=" + id));

        IncidentStatus statusEnum;
        try {
            statusEnum = IncidentStatus.valueOf(newStatus.toUpperCase());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Status invalide: " + newStatus);
        }

        inc.setStatus(statusEnum);
        return incidentRepository.save(inc);
    }

    @Override
    public List<Incident> getIncidentsForVehicle(Long vehicleId) {
        return incidentRepository.findByVehicleId(vehicleId);
    }

    @Override
    public List<Incident> getOpenIncidents() {
        return incidentRepository.findByStatus(IncidentStatus.OPEN);
    }

    @Override
    public Incident getById(Long id) {
        return incidentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Incident introuvable id=" + id));
    }

    @Override
    public List<Incident> searchIncidents(
            String status,
            String severity,
            Long vehicleId,
            String reportedBy,
            Long lineId,
            LocalDateTime fromDate,
            LocalDateTime toDate
    ) {
        // sans query => on filtre en mémoire
        List<Incident> all = incidentRepository.findAll();

        return all.stream()
                .filter(i -> status == null || i.getStatus().name().equalsIgnoreCase(status))
                .filter(i -> severity == null || i.getSeverity().name().equalsIgnoreCase(severity))
                .filter(i -> vehicleId == null || (i.getVehicleId() != null && i.getVehicleId().equals(vehicleId)))
                .filter(i -> lineId == null || (i.getLineId() != null && i.getLineId().equals(lineId)))
                .filter(i -> reportedBy == null || (i.getReportedBy() != null && i.getReportedBy().equalsIgnoreCase(reportedBy)))
                .filter(i -> {
                    if (fromDate == null && toDate == null) return true;
                    Instant t = i.getReportedAt();
                    if (t == null) return false;
                    LocalDateTime ldt = LocalDateTime.ofInstant(t, ZoneOffset.UTC);
                    if (fromDate != null && ldt.isBefore(fromDate)) return false;
                    if (toDate != null && ldt.isAfter(toDate)) return false;
                    return true;
                })
                .toList();
    }

    @Override
    public List<Incident> getRecentIncidents(int limit) {
        return incidentRepository.findAllByOrderByReportedAtDesc(PageRequest.of(0, limit));
    }

    @Override
    public List<Incident> getIncidentsForReporter(String reportedBy) {
        return incidentRepository.findByReportedBy(reportedBy);
    }

    @Override
    public List<Incident> getIncidentsForLine(Long lineId) {
        return incidentRepository.findByLineId(lineId);
    }

    @Override
    public Map<String, Long> getIncidentCountByStatus() {
        return incidentRepository.findAll().stream()
                .collect(Collectors.groupingBy(i -> i.getStatus().name(), Collectors.counting()));
    }

    @Override
    public void deleteIncident(Long id) {
        if (!incidentRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Incident introuvable id=" + id);
        }
        incidentRepository.deleteById(id);
    }
}
