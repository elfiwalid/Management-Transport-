package com.pfa.service_sinistre.Service;

import com.pfa.service_sinistre.entity.Incident;

import java.util.List;

public interface IncidentService {

    Incident reportIncident(Incident incident);

    Incident updateStatus(Long id, String newStatus);

    List<Incident> getIncidentsForVehicle(Long vehicleId);

    List<Incident> getOpenIncidents();

    Incident getById(Long id);
}
