package com.pfa.service_sinistre.Service;


import com.pfa.service_sinistre.entity.Alert;
import com.pfa.service_sinistre.entity.AlertSeverity;
import com.pfa.service_sinistre.entity.AlertType;
import com.pfa.service_sinistre.kafka.VehiclePositionEvent;

import java.util.List;

public interface AlertService {

    Alert createDelayAlert(Long vehicleId, Long lineId, Long stopId, int delayMinutes);

    Alert createIncidentAlert(Long incidentId, Long vehicleId, Long lineId, Long stopId);

    Alert createNoSignalAlert(Long vehicleId);

    Alert updateStatus(Long alertId, String newStatus);

    List<Alert> getOpenAlerts();

    List<Alert> getAlertsForVehicle(Long vehicleId);
    void handleVehiclePositionEvent(VehiclePositionEvent event);
}
