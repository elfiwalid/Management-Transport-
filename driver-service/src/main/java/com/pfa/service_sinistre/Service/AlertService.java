package com.pfa.service_sinistre.Service;

import com.pfa.service_sinistre.entity.Alert;
import com.pfa.service_sinistre.kafka.VehiclePositionEvent;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface AlertService {

    // =========================
    //   Création d'alertes métier
    // =========================
    Alert createDelayAlert(Long vehicleId, Long lineId, Long stopId, int delayMinutes);

    Alert createIncidentAlert(Long incidentId, Long vehicleId, Long lineId, Long stopId);

    Alert createNoSignalAlert(Long vehicleId);

    // =========================
    //   CRUD / Actions
    // =========================
    Alert createAlert(Alert alert);

    Alert getById(Long id);

    Alert updateAlert(Long id, Alert alertUpdate);

    void deleteAlert(Long id);

    Alert updateStatus(Long alertId, String newStatus);

    // =========================
    //   Lecture simple
    // =========================
    List<Alert> getOpenAlerts();

    List<Alert> getAlertsForVehicle(Long vehicleId);

    List<Alert> getAlertsForLine(Long lineId);

    List<Alert> getAlertsForStop(Long stopId);

    List<Alert> getRecentAlerts(int limit);

    // =========================
    //   Recherche & Stats
    // =========================
    List<Alert> searchAlerts(
            String status,
            String severity,
            Long vehicleId,
            Long lineId,
            Long stopId,
            LocalDateTime fromDate,
            LocalDateTime toDate
    );

    Map<String, Long> getAlertCountByStatus();

    Map<String, Long> getAlertCountBySeverity();

    // =========================
    //   Kafka consumer handler
    // =========================
    void handleVehiclePositionEvent(VehiclePositionEvent event);
}
