package com.pfa.service_sinistre.DTO;

import java.time.LocalDate;
import java.time.LocalTime;

public record PlannedStopTimeResponse(
        Long id,
        Long lineId,
        Long routeId,
        String tripCode,
        Long stopId,
        LocalDate serviceDate,
        LocalTime plannedArrivalTime,
        LocalTime plannedDepartureTime,
        Integer stopSequence,
        boolean active
) {}
