package com.pfa.service_sinistre.Service;


import com.pfa.service_sinistre.Repository.AlertRepository;
import com.pfa.service_sinistre.entity.*;
import com.pfa.service_sinistre.kafka.VehiclePositionEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AlertServiceImpl implements AlertService {

    private final AlertRepository alertRepository;

    @Override
    public Alert createDelayAlert(Long vehicleId, Long lineId, Long stopId, int delayMinutes) {

        AlertSeverity severity =
                (delayMinutes >= 10) ?
                        AlertSeverity.CRITICAL :
                        AlertSeverity.WARNING;

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

    @Override
    public Alert updateStatus(Long alertId, String newStatus) {
        Alert alert = alertRepository.findById(alertId)
                .orElseThrow(() -> new RuntimeException("Alerte introuvable"));

        alert.setStatus(AlertStatus.valueOf(newStatus.toUpperCase()));
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
    public void handleVehiclePositionEvent(VehiclePositionEvent event) {
        log.info("💡 Traitement métier de l'événement véhicule : {}", event);

        // 1️⃣ Si pas de retard renseigné → rien à faire pour l’instant
        if (event.getDelayMinutes() == null) {
            log.info("Aucun delayMinutes dans l'événement, aucune alerte générée.");
            return;
        }

        int delay = event.getDelayMinutes();

        // 2️⃣ Exemple de règle métier simple :
        //    - < 5 min : on ignore
        //    - 5 à 10 min : WARNING
        //    - > 10 min : CRITICAL
        if (delay < 5) {
            log.info("Retard {} min < 5 min, pas d'alerte générée.", delay);
            return;
        }

        AlertSeverity severity =
                delay < 10 ? AlertSeverity.WARNING : AlertSeverity.CRITICAL;

        String title = "Retard de " + delay + " min sur la ligne " + event.getLineId();
        String details = "Le véhicule " + event.getVehicleId()
                + " a un retard estimé de " + delay + " minutes sur la ligne "
                + event.getLineId() + ".";

        Alert alert = Alert.builder()
                .type(AlertType.DELAY)
                .severity(severity)
                .status(AlertStatus.OPEN)
                .vehicleId(event.getVehicleId())
                .lineId(event.getLineId())
                .tripId(null)      // à remplir plus tard quand tu auras la notion de trip
                .stopId(null)      // idem si tu veux le stop concerné
                .delayMinutes(delay)
                .incidentId(null)  // si lié à Incident plus tard
                .title(title)
                .details(details)
                .createdAt(Instant.now())
                .build();

        alertRepository.save(alert);

        log.info("🚨 Alerte créée et sauvegardée en BDD : {}", alert);
    }
}
