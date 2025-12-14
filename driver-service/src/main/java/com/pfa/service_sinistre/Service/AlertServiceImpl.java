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
import org.springframework.scheduling.annotation.Scheduled;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AlertServiceImpl implements AlertService {

    private final AlertRepository alertRepository;
    private final ConcurrentMap<Long, Instant> lastSeenByVehicle = new ConcurrentHashMap<>();

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
        lastSeenByVehicle.put(event.getVehicleId(), Instant.now());

        log.info("💡 Traitement Kafka vehicle.positions : {}", event);

        if (event.getVehicleId() == null) return;

        Integer delayObj = event.getDelayMinutes();
        if (delayObj == null) {
            log.info("delayMinutes null => rien");
            return;
        }

        int delay = delayObj;

        // ✅ Si retard < 5 => on peut fermer une alerte DELAY ouverte (optionnel)
        if (delay < 5) {
            closeOpenDelayIfExists(event.getVehicleId(), event.getLineId());
            return;
        }

        AlertSeverity sev = delay < 10 ? AlertSeverity.WARNING : AlertSeverity.CRITICAL;

        // ✅ 1) est-ce qu'il existe déjà une alerte DELAY OPEN pour ce véhicule+ligne ?
        Optional<Alert> existingOpt = alertRepository
                .findFirstByTypeAndStatusAndVehicleIdAndLineIdOrderByCreatedAtDesc(
                        AlertType.DELAY, AlertStatus.OPEN, event.getVehicleId(), event.getLineId()
                );

        if (existingOpt.isPresent()) {
            Alert existing = existingOpt.get();

            // ✅ update seulement si le retard a changé (ou a augmenté)
            Integer oldDelay = existing.getDelayMinutes();
            if (oldDelay == null || delay != oldDelay) {

                existing.setDelayMinutes(delay);
                existing.setSeverity(sev);
                existing.setTitle("Retard détecté");
                existing.setDetails("Véhicule " + event.getVehicleId()
                        + " retard " + delay + " min sur ligne " + event.getLineId());

                // (optionnel) update stopId si tu l’as plus tard
                // existing.setStopId(...)

                alertRepository.save(existing);
                log.info("🔁 Alerte DELAY mise à jour (id={}) => delay={}min", existing.getId(), delay);
            } else {
                log.info("⏭️ Même delay ({}) => pas de mise à jour (anti-spam)", delay);
            }
            return;
        }

        // ✅ 2) Sinon: créer une nouvelle alerte DELAY
        Alert alert = Alert.builder()
                .type(AlertType.DELAY)
                .severity(sev)
                .status(AlertStatus.OPEN)
                .vehicleId(event.getVehicleId())
                .lineId(event.getLineId())
                .delayMinutes(delay)
                .title("Retard détecté")
                .details("Véhicule " + event.getVehicleId()
                        + " retard " + delay + " min sur ligne " + event.getLineId())
                .createdAt(Instant.now())
                .build();

        alertRepository.save(alert);
        log.info("🚨 Nouvelle alerte DELAY créée (id={})", alert.getId());
    }

    private void closeOpenDelayIfExists(Long vehicleId, Long lineId) {
        if (vehicleId == null || lineId == null) return;

        Optional<Alert> existingOpt = alertRepository
                .findFirstByTypeAndStatusAndVehicleIdAndLineIdOrderByCreatedAtDesc(
                        AlertType.DELAY, AlertStatus.OPEN, vehicleId, lineId
                );

        existingOpt.ifPresent(a -> {
            a.setStatus(AlertStatus.CLOSED);
            a.setDetails((a.getDetails() == null ? "" : a.getDetails() + " | ")
                    + "Retard redevenu normal (<5min). Fermeture auto.");
            alertRepository.save(a);
            log.info("✅ Alerte DELAY fermée automatiquement (id={})", a.getId());
        });
    }


    @Scheduled(fixedRate = 60_000) // chaque 1 minute
    public void detectNoSignal() {
        Instant now = Instant.now();

        // seuil : 25 minutes (tu peux le changer)
        long thresholdSeconds = 25 * 60;

        for (Map.Entry<Long, Instant> e : lastSeenByVehicle.entrySet()) {
            Long vehicleId = e.getKey();
            Instant last = e.getValue();
            if (last == null) continue;

            long diff = now.getEpochSecond() - last.getEpochSecond();
            if (diff > thresholdSeconds) {
                createNoSignalOnce(vehicleId);
            }
        }
    }

    private void createNoSignalOnce(Long vehicleId) {
        // ne pas spammer: si déjà OPEN, ne rien faire
        Optional<Alert> existing = alertRepository
                .findFirstByTypeAndStatusAndVehicleIdOrderByCreatedAtDesc(
                        AlertType.NO_SIGNAL, AlertStatus.OPEN, vehicleId
                );
        if (existing.isPresent()) return;

        Alert alert = Alert.builder()
                .type(AlertType.NO_SIGNAL)
                .severity(AlertSeverity.WARNING)
                .status(AlertStatus.OPEN)
                .vehicleId(vehicleId)
                .title("Perte de signal")
                .details("Aucun message position reçu depuis > 25 min.")
                .createdAt(Instant.now())
                .build();

        alertRepository.save(alert);
        log.info("📡 Alerte NO_SIGNAL créée pour vehicle {}", vehicleId);
    }

}
