package com.pfa.service_sinistre.Service;

import com.pfa.service_sinistre.Repository.AlertRepository;
import com.pfa.service_sinistre.entity.*;
import com.pfa.service_sinistre.kafka.VehiclePositionEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AlertServiceImpl implements AlertService {

    private final AlertRepository alertRepository;

    private Instant toInstant(LocalDateTime dt) {
        return dt == null ? null : dt.toInstant(ZoneOffset.UTC);
    }

    // =========================
    //   Création d'alertes
    // =========================

    @Override
    public Alert createDelayAlert(Long vehicleId, Long lineId, Long stopId, int delayMinutes) {

        AlertSeverity severity = (delayMinutes >= 10)
                ? AlertSeverity.CRITICAL
                : AlertSeverity.WARNING;

        Alert alert = Alert.builder()
                .type(AlertType.DELAY)
                .severity(severity)
                .status(AlertStatus.OPEN)
                .vehicleId(vehicleId)
                .lineId(lineId)
                .stopId(stopId)
                .delayMinutes(delayMinutes)
                .title("Retard détecté")
                .details("Retard de " + delayMinutes + " minutes")
                .createdAt(Instant.now())
                .build();

        return alertRepository.save(alert);
    }

    @Override
    public Alert createIncidentAlert(Long incidentId, Long vehicleId, Long lineId, Long stopId) {

        Alert alert = Alert.builder()
                .type(AlertType.INCIDENT)
                .severity(AlertSeverity.CRITICAL)
                .status(AlertStatus.OPEN)
                .vehicleId(vehicleId)
                .lineId(lineId)
                .stopId(stopId)
                .incidentId(incidentId)
                .title("Incident déclaré")
                .details("Incident ID: " + incidentId)
                .createdAt(Instant.now())
                .build();

        return alertRepository.save(alert);
    }

    @Override
    public Alert createNoSignalAlert(Long vehicleId) {

        Alert alert = Alert.builder()
                .type(AlertType.NO_SIGNAL)
                .severity(AlertSeverity.WARNING)
                .status(AlertStatus.OPEN)
                .vehicleId(vehicleId)
                .title("Perte de signal GPS")
                .details("Aucune position reçue depuis plus de X minutes")
                .createdAt(Instant.now())
                .build();

        return alertRepository.save(alert);
    }

    // =========================
    //   CRUD / Lecture
    // =========================

    @Override
    public Alert createAlert(Alert alert) {
        if (alert.getStatus() == null) alert.setStatus(AlertStatus.OPEN);
        if (alert.getCreatedAt() == null) alert.setCreatedAt(Instant.now());
        if (alert.getType() == null) throw new RuntimeException("type obligatoire");
        if (alert.getSeverity() == null) throw new RuntimeException("severity obligatoire");
        return alertRepository.save(alert);
    }

    @Override
    public Alert getById(Long id) {
        return alertRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Alerte introuvable"));
    }

    @Override
    public Alert updateAlert(Long id, Alert alertUpdate) {
        Alert existing = getById(id);

        // update fields (null => keep)
        if (alertUpdate.getType() != null) existing.setType(alertUpdate.getType());
        if (alertUpdate.getSeverity() != null) existing.setSeverity(alertUpdate.getSeverity());
        if (alertUpdate.getStatus() != null) existing.setStatus(alertUpdate.getStatus());

        if (alertUpdate.getVehicleId() != null) existing.setVehicleId(alertUpdate.getVehicleId());
        if (alertUpdate.getLineId() != null) existing.setLineId(alertUpdate.getLineId());
        if (alertUpdate.getStopId() != null) existing.setStopId(alertUpdate.getStopId());
        if (alertUpdate.getTripId() != null) existing.setTripId(alertUpdate.getTripId());

        if (alertUpdate.getDelayMinutes() != null) existing.setDelayMinutes(alertUpdate.getDelayMinutes());
        if (alertUpdate.getIncidentId() != null) existing.setIncidentId(alertUpdate.getIncidentId());

        if (alertUpdate.getTitle() != null) existing.setTitle(alertUpdate.getTitle());
        if (alertUpdate.getDetails() != null) existing.setDetails(alertUpdate.getDetails());

        // createdAt ne change pas
        return alertRepository.save(existing);
    }

    @Override
    public void deleteAlert(Long id) {
        if (!alertRepository.existsById(id)) {
            throw new RuntimeException("Alerte introuvable");
        }
        alertRepository.deleteById(id);
    }

    // =========================
    //   Endpoints existants
    // =========================


    @Override
    public Alert updateStatus(Long alertId, String newStatus) {

        Alert alert = alertRepository.findById(alertId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Alerte introuvable id=" + alertId
                ));

        AlertStatus statusEnum;
        try {
            statusEnum = AlertStatus.valueOf(newStatus.toUpperCase());
        } catch (Exception e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Status invalide: " + newStatus + " | valeurs possibles=" + java.util.Arrays.toString(AlertStatus.values())
            );
        }

        alert.setStatus(statusEnum);
        return alertRepository.save(alert);
    }

    @Override
    public List<Alert> getOpenAlerts() {
        return alertRepository.findByStatus(AlertStatus.OPEN);
    }

    @Override
    public List<Alert> getAlertsForVehicle(Long vehicleId) {
        return alertRepository.findByVehicleIdAndStatus(vehicleId, AlertStatus.OPEN);
    }

    @Override
    public List<Alert> getAlertsForLine(Long lineId) {
        return alertRepository.findByLineId(lineId);
    }

    @Override
    public List<Alert> getAlertsForStop(Long stopId) {
        return alertRepository.findByStopId(stopId);
    }

    @Override
    public List<Alert> getRecentAlerts(int limit) {
        int safeLimit = Math.max(1, Math.min(limit, 100));
        return alertRepository.findAllByOrderByCreatedAtDesc(PageRequest.of(0, safeLimit));
    }

    /**
     * ✅ search sans @Query :
     * on récupère tout puis on filtre côté Java.
     * (OK pour projet PFA / volume raisonnable)
     */
    @Override
    public List<Alert> searchAlerts(
            String status,
            String severity,
            Long vehicleId,
            Long lineId,
            Long stopId,
            LocalDateTime fromDate,
            LocalDateTime toDate
    ) {
        AlertStatus st = (status == null || status.isBlank()) ? null : AlertStatus.valueOf(status.toUpperCase());
        AlertSeverity sev = (severity == null || severity.isBlank()) ? null : AlertSeverity.valueOf(severity.toUpperCase());

        Instant from = toInstant(fromDate);
        Instant to = toInstant(toDate);

        return alertRepository.findAll().stream()
                .filter(a -> st == null || a.getStatus() == st)
                .filter(a -> sev == null || a.getSeverity() == sev)
                .filter(a -> vehicleId == null || Objects.equals(a.getVehicleId(), vehicleId))
                .filter(a -> lineId == null || Objects.equals(a.getLineId(), lineId))
                .filter(a -> stopId == null || Objects.equals(a.getStopId(), stopId))
                .filter(a -> from == null || (a.getCreatedAt() != null && !a.getCreatedAt().isBefore(from)))
                .filter(a -> to == null || (a.getCreatedAt() != null && !a.getCreatedAt().isAfter(to)))
                .sorted(Comparator.comparing(Alert::getCreatedAt, Comparator.nullsLast(Comparator.naturalOrder())).reversed())
                .toList();
    }

    @Override
    public Map<String, Long> getAlertCountByStatus() {
        return alertRepository.findAll().stream()
                .filter(a -> a.getStatus() != null)
                .collect(Collectors.groupingBy(
                        a -> a.getStatus().name(),
                        Collectors.counting()
                ));
    }

    @Override
    public Map<String, Long> getAlertCountBySeverity() {
        return alertRepository.findAll().stream()
                .filter(a -> a.getSeverity() != null)
                .collect(Collectors.groupingBy(
                        a -> a.getSeverity().name(),
                        Collectors.counting()
                ));
    }

    // =========================
    //   Kafka event
    // =========================

    @Override
    public void handleVehiclePositionEvent(VehiclePositionEvent event) {
        log.info("💡 Traitement métier de l'événement véhicule : {}", event);

        if (event.getDelayMinutes() == null) {
            log.info("Aucun delayMinutes, aucune alerte générée.");
            return;
        }

        int delay = event.getDelayMinutes();
        if (delay < 5) {
            log.info("Retard {} min < 5 min, pas d'alerte.", delay);
            return;
        }

        AlertSeverity sev = delay < 10 ? AlertSeverity.WARNING : AlertSeverity.CRITICAL;

        Alert alert = Alert.builder()
                .type(AlertType.DELAY)
                .severity(sev)
                .status(AlertStatus.OPEN)
                .vehicleId(event.getVehicleId())
                .lineId(event.getLineId())
                .delayMinutes(delay)
                .title("Retard de " + delay + " min sur la ligne " + event.getLineId())
                .details("Le véhicule " + event.getVehicleId() + " a un retard estimé de " + delay
                        + " minutes sur la ligne " + event.getLineId() + ".")
                .createdAt(Instant.now())
                .build();

        alertRepository.save(alert);
        log.info("🚨 Alerte créée : {}", alert);
    }
}
