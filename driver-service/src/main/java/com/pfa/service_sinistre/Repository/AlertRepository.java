package com.pfa.service_sinistre.Repository;

import com.pfa.service_sinistre.entity.Alert;
import com.pfa.service_sinistre.entity.AlertStatus;
import com.pfa.service_sinistre.entity.AlertType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AlertRepository extends JpaRepository<Alert, Long> {

    List<Alert> findByStatus(AlertStatus status);

    List<Alert> findByVehicleIdAndStatus(Long vehicleId, AlertStatus status);

    List<Alert> findByTypeAndStatus(AlertType type, AlertStatus status);
}
