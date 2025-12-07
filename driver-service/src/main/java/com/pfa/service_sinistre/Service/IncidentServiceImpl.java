package com.pfa.service_sinistre.Service;


import com.pfa.service_sinistre.entity.Incident;
import com.pfa.service_sinistre.entity.IncidentStatus;
import com.pfa.service_sinistre.repository.IncidentRepository;
import com.pfa.service_sinistre.Service.IncidentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class IncidentServiceImpl implements IncidentService {

    private final IncidentRepository incidentRepository;

    @Override
    public Incident reportIncident(Incident incident) {
        incident.setStatus(IncidentStatus.OPEN);
        incident.setReportedAt(Instant.now());
        return incidentRepository.save(incident);
    }

    @Override
    public Incident updateStatus(Long id, String newStatus) {
        Incident inc = incidentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Incident non trouvé"));

        inc.setStatus(IncidentStatus.valueOf(newStatus.toUpperCase()));
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
                .orElseThrow(() -> new RuntimeException("Incident introuvable"));
    }
}
