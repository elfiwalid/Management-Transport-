package com.pfa.service_sinistre.Controller;

import com.pfa.service_sinistre.entity.Incident;
import com.pfa.service_sinistre.Service.IncidentService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;


import java.util.List;
import java.util.Map;

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


    // =========================
    //     Endpoints enrichis
    // =========================

    /**
     * Recherche d'incidents avec filtres optionnels :
     * - status     (OPEN, RESOLVED, ...)
     * - severity   (MINOR, MAJOR, CRITICAL) si ton entity a ça
     * - vehicleId
     * - driverId
     * - lineId
     * - fromDate / toDate  (filtre par période)
     *
     * Exemple :
     *   GET /driver/incidents?status=OPEN&lineId=1
     */

    /**
     * Derniers incidents pour le dashboard admin.
     *
     * Exemple :
     *   GET /driver/incidents/recent?limit=10
     */
    @GetMapping("/recent")
    public ResponseEntity<List<Incident>> getRecentIncidents(
            @RequestParam(defaultValue = "10") int limit
    ) {
        return ResponseEntity.ok(incidentService.getRecentIncidents(limit));
    }

    /**
     * Incidents d’un conducteur donné.
     *
     * Exemple :
     *   GET /driver/incidents/driver/5
     */


    /**
     * Incidents d’une ligne.
     *
     * Exemple :
     *   GET /driver/incidents/line/3
     */
    @GetMapping("/line/{lineId}")
    public ResponseEntity<List<Incident>> getByLine(@PathVariable Long lineId) {
        return ResponseEntity.ok(incidentService.getIncidentsForLine(lineId));
    }

    /**
     * Statistiques des incidents par statut.
     * Retourne par ex :
     * {
     *   "OPEN": 5,
     *   "IN_PROGRESS": 2,
     *   "RESOLVED": 12
     * }
     *
     * utile pour le dashboard.
     */
    @GetMapping("/stats/status")
    public ResponseEntity<Map<String, Long>> getStatsByStatus() {
        return ResponseEntity.ok(incidentService.getIncidentCountByStatus());
    }

    /**
     * (Optionnel) Suppression d’un incident.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteIncident(@PathVariable Long id) {
        incidentService.deleteIncident(id);
        return ResponseEntity.noContent().build();
    }





    @GetMapping("/driver/{reportedBy}")
    public ResponseEntity<List<Incident>> getByReporter(@PathVariable String reportedBy) {
        return ResponseEntity.ok(incidentService.getIncidentsForReporter(reportedBy));
    }

    @GetMapping
    public ResponseEntity<List<Incident>> searchIncidents(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String severity,
            @RequestParam(required = false) Long vehicleId,
            @RequestParam(required = false) String reportedBy,
            @RequestParam(required = false) Long lineId,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fromDate,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime toDate
    ) {
        return ResponseEntity.ok(
                incidentService.searchIncidents(status, severity, vehicleId, reportedBy, lineId, fromDate, toDate)
        );
    }
}
