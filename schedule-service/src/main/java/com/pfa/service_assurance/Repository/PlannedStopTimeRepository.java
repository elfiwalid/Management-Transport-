package com.pfa.service_assurance.Repository;

import com.pfa.service_assurance.Entity.PlannedStopTime;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface PlannedStopTimeRepository extends JpaRepository<PlannedStopTime, Long> {

    // Tous les horaires d'une ligne pour un jour
    List<PlannedStopTime> findByLineIdAndServiceDateOrderByTripCodeAscStopSequenceAsc(
            Long lineId,
            LocalDate serviceDate
    );

    // Tous les horaires pour un arrêt pour un jour donné
    List<PlannedStopTime> findByStopIdAndServiceDateOrderByPlannedArrivalTimeAsc(
            Long stopId,
            LocalDate serviceDate
    );
}
