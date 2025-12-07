package com.pfa.service_sinistre.Controller;

import com.pfa.service_sinistre.entity.Incident;
import com.pfa.service_sinistre.Service.IncidentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/driver/incidents")
@RequiredArgsConstructor
public class IncidentController {

    private final IncidentService incidentService;

    @PostMapping
    public Incident reportIncident(@RequestBody Incident incident) {
        return incidentService.reportIncident(incident);
    }

    @GetMapping("/{id}")
    public Incident getById(@PathVariable Long id) {
        return incidentService.getById(id);
    }

    @GetMapping("/vehicle/{vehicleId}")
    public List<Incident> getByVehicle(@PathVariable Long vehicleId) {
        return incidentService.getIncidentsForVehicle(vehicleId);
    }

    @GetMapping("/open")
    public List<Incident> getOpenIncidents() {
        return incidentService.getOpenIncidents();
    }

    @PutMapping("/{id}/status")
    public Incident updateStatus(@PathVariable Long id, @RequestParam String status) {
        return incidentService.updateStatus(id, status);
    }
}
