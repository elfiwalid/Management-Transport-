package com.pfa.service_sinistre.Repository;

import com.pfa.service_sinistre.entity.Alert;
import com.pfa.service_sinistre.entity.AlertStatus;
import com.pfa.service_sinistre.entity.AlertType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface AlertRepository extends JpaRepository<Alert, Long> {

    List<Alert> findByStatus(AlertStatus status);

    List<Alert> findByVehicleIdAndStatus(Long vehicleId, AlertStatus status);

    List<Alert> findByTypeAndStatus(AlertType type, AlertStatus status);

    // ✅ ajoutés pour tes endpoints
    List<Alert> findByLineId(Long lineId);

    List<Alert> findByStopId(Long stopId);

    // ✅ recent (utilisable avec PageRequest.of(0, limit))
    List<Alert> findAllByOrderByCreatedAtDesc(Pageable pageable);

    // ✅ utile si tu veux filtrer uniquement par date (optionnel)
    List<Alert> findByCreatedAtBetween(Instant from, Instant to);
    // ✅ NEW: retrouver une alerte DELAY déjà ouverte
    Optional<Alert> findFirstByTypeAndStatusAndVehicleIdAndLineIdOrderByCreatedAtDesc(
            AlertType type, AlertStatus status, Long vehicleId, Long lineId
    );

    // ✅ NEW: retrouver NO_SIGNAL ouvert
    Optional<Alert> findFirstByTypeAndStatusAndVehicleIdOrderByCreatedAtDesc(
            AlertType type, AlertStatus status, Long vehicleId
    );
}
