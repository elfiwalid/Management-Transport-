package com.pfa.service_sinistre.Controller;


import com.pfa.service_sinistre.entity.Alert;
import com.pfa.service_sinistre.Service.AlertService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/driver/alerts")
@RequiredArgsConstructor
public class AlertController {

    private final AlertService alertService;

    @GetMapping("/open")
    public List<Alert> getOpenAlerts() {
        return alertService.getOpenAlerts();
    }

    @GetMapping("/vehicle/{vehicleId}")
    public List<Alert> getAlertsForVehicle(@PathVariable Long vehicleId) {
        return alertService.getAlertsForVehicle(vehicleId);
    }

    @PutMapping("/{id}/status")
    public Alert updateStatus(@PathVariable Long id, @RequestParam String status) {
        return alertService.updateStatus(id, status);
    }

    /**
     * Suppression d'une alerte (optionnel, plutôt pour admin).
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAlert(@PathVariable Long id) {
        alertService.deleteAlert(id);
        return ResponseEntity.noContent().build();
    }

    // =========================
    //     Lecture avancée
    // =========================



    /**
     * Toutes les alertes d’une ligne.
     */
    @GetMapping("/line/{lineId}")
    public ResponseEntity<List<Alert>> getAlertsForLine(@PathVariable Long lineId) {
        return ResponseEntity.ok(alertService.getAlertsForLine(lineId));
    }

    /**
     * Toutes les alertes pour un arrêt donné.
     */
    @GetMapping("/stop/{stopId}")
    public ResponseEntity<List<Alert>> getAlertsForStop(@PathVariable Long stopId) {
        return ResponseEntity.ok(alertService.getAlertsForStop(stopId));
    }

    /**
     * Dernières alertes (par exemple pour afficher les 10 plus récentes dans un dashboard).
     * Exemple : GET /driver/alerts/recent?limit=10
     */
    @GetMapping("/recent")
    public ResponseEntity<List<Alert>> getRecentAlerts(
            @RequestParam(defaultValue = "10") int limit
    ) {
        return ResponseEntity.ok(alertService.getRecentAlerts(limit));
    }

    @GetMapping
    public ResponseEntity<List<Alert>> searchAlerts(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String severity,
            @RequestParam(required = false) Long vehicleId,
            @RequestParam(required = false) Long lineId,
            @RequestParam(required = false) Long stopId,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fromDate,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime toDate
    ) {
        return ResponseEntity.ok(
                alertService.searchAlerts(status, severity, vehicleId, lineId, stopId, fromDate, toDate)
        );
    }

    @GetMapping("/stats/status")
    public ResponseEntity<Map<String, Long>> getStatsByStatus() {
        return ResponseEntity.ok(alertService.getAlertCountByStatus());
    }

    @GetMapping("/stats/severity")
    public ResponseEntity<Map<String, Long>> getStatsBySeverity() {
        return ResponseEntity.ok(alertService.getAlertCountBySeverity());
    }
}
