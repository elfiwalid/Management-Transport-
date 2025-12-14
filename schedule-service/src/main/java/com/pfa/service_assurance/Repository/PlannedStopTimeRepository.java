package com.pfa.service_assurance.Repository;

import com.pfa.service_assurance.Entity.PlannedStopTime;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface PlannedStopTimeRepository extends JpaRepository<PlannedStopTime, Long> {

    List<PlannedStopTime> findByLineIdAndServiceDateOrderByTripCodeAscStopSequenceAsc(
            Long lineId, LocalDate serviceDate
    );

    List<PlannedStopTime> findByStopIdAndServiceDateOrderByPlannedArrivalTimeAsc(
            Long stopId, LocalDate serviceDate
    );

    // line + stop + date (tri time)
    List<PlannedStopTime> findByLineIdAndStopIdAndServiceDateOrderByPlannedArrivalTimeAsc(
            Long lineId, Long stopId, LocalDate serviceDate
    );

    // search combos
    List<PlannedStopTime> findByLineIdAndServiceDateOrderByPlannedArrivalTimeAsc(Long lineId, LocalDate date);

    // next departures (arrival/departure >= fromTime)
    List<PlannedStopTime> findByLineIdAndStopIdAndServiceDateAndPlannedDepartureTimeGreaterThanEqualOrderByPlannedDepartureTimeAsc(
            Long lineId, Long stopId, LocalDate date, LocalTime fromTime, Pageable pageable
    );

    // si plannedDepartureTime est null, on peut fallback sur plannedArrivalTime
    List<PlannedStopTime> findByLineIdAndStopIdAndServiceDateAndPlannedArrivalTimeGreaterThanEqualOrderByPlannedArrivalTimeAsc(
            Long lineId, Long stopId, LocalDate date, LocalTime fromTime, Pageable pageable
    );
}
