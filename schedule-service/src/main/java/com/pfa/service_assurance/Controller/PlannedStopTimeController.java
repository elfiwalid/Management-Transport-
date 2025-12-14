package com.pfa.service_assurance.Controller;

import com.pfa.service_assurance.DTO.CreatePlannedStopTimeRequest;
import com.pfa.service_assurance.DTO.PlannedStopTimeResponse;
import com.pfa.service_assurance.Service.PlannedStopTimeService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalTime;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/schedule")
@RequiredArgsConstructor
public class PlannedStopTimeController {

    private final PlannedStopTimeService plannedStopTimeService;

    // Créer un horaire planifié
    @PostMapping
    public ResponseEntity<PlannedStopTimeResponse> create(
            @RequestBody CreatePlannedStopTimeRequest request) {
        return ResponseEntity.ok(plannedStopTimeService.create(request));
    }

    // Récupérer un horaire par id
    @GetMapping("/{id}")
    public ResponseEntity<PlannedStopTimeResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(plannedStopTimeService.getById(id));
    }

    // Tous les horaires d'une ligne à une date
    @GetMapping("/line/{lineId}")
    public ResponseEntity<List<PlannedStopTimeResponse>> getForLine(
            @PathVariable Long lineId,
            @RequestParam("date")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate serviceDate) {

        return ResponseEntity.ok(plannedStopTimeService.getForLine(lineId, serviceDate));
    }

    // Tous les horaires pour un arrêt à une date
    @GetMapping("/stop/{stopId}")
    public ResponseEntity<List<PlannedStopTimeResponse>> getForStop(
            @PathVariable Long stopId,
            @RequestParam("date")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate serviceDate) {

        return ResponseEntity.ok(plannedStopTimeService.getForStop(stopId, serviceDate));
    }

    // Mise à jour d'un horaire
    @PutMapping("/{id}")
    public ResponseEntity<PlannedStopTimeResponse> update(
            @PathVariable Long id,
            @RequestBody CreatePlannedStopTimeRequest request) {

        return ResponseEntity.ok(plannedStopTimeService.update(id, request));
    }

    // Suppression d'un horaire
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        plannedStopTimeService.delete(id);
        return ResponseEntity.noContent().build();
    }



    // =========================
    //   Endpoints "front-friendly"
    // =========================

    /**
     * Liste des horaires avec filtres optionnels :
     * - lineId (id de la ligne)
     * - stopId (id de l'arrêt)
     * - date (jour de service)
     *
     * Exemple front :
     *   GET /schedule?lineId=1&date=2025-12-08
     */
    @GetMapping
    public ResponseEntity<List<PlannedStopTimeResponse>> search(
            @RequestParam(required = false) Long lineId,
            @RequestParam(required = false) Long stopId,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        return ResponseEntity.ok(plannedStopTimeService.search(lineId, stopId, date));
    }

    /**
     * Tous les horaires pour une ligne + un arrêt à une date,
     * triés par heure (utile pour la vue détaillée).
     *
     * Exemple :
     *   GET /schedule/line/1/stop/5?date=2025-12-08
     */
    @GetMapping("/line/{lineId}/stop/{stopId}")
    public ResponseEntity<List<PlannedStopTimeResponse>> getForLineAndStop(
            @PathVariable Long lineId,
            @PathVariable Long stopId,
            @RequestParam("date")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate serviceDate) {

        return ResponseEntity.ok(
                plannedStopTimeService.getForLineAndStop(lineId, stopId, serviceDate)
        );
    }

    /**
     * Prochains départs pour un arrêt d'une ligne.
     *
     * Paramètres :
     *  - lineId  : ligne concernée
     *  - stopId  : arrêt concerné
     *  - date    : jour de service
     *  - fromTime: heure actuelle (ex: 14:30)
     *  - limit   : nombre de départs à retourner (par défaut 5)
     *
     * Exemple :
     *   GET /schedule/next?lineId=1&stopId=5&date=2025-12-08&fromTime=14:30&limit=3
     *
     * Utilisation front :
     *   → écran "Temps réel" / "Prochains bus"
     */
    @GetMapping("/next")
    public ResponseEntity<List<PlannedStopTimeResponse>> getNextDepartures(
            @RequestParam Long lineId,
            @RequestParam Long stopId,
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam
            @DateTimeFormat(pattern = "HH:mm") LocalTime fromTime,
            @RequestParam(defaultValue = "5") int limit) {

        return ResponseEntity.ok(
                plannedStopTimeService.getNextDepartures(lineId, stopId, date, fromTime, limit)
        );
    }


}
