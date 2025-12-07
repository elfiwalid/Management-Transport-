package com.pfa.service_assurance.Controller;

import com.pfa.service_assurance.DTO.CreatePlannedStopTimeRequest;
import com.pfa.service_assurance.DTO.PlannedStopTimeResponse;
import com.pfa.service_assurance.Service.PlannedStopTimeService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/schedule")
@RequiredArgsConstructor
@CrossOrigin
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


}
