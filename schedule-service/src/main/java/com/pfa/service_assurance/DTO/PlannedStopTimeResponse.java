package com.pfa.service_assurance.DTO;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
public class PlannedStopTimeResponse {

    private Long id;

    private Long lineId;
    private Long routeId;
    private String tripCode;
    private Long stopId;

    private LocalDate serviceDate;

    private LocalTime plannedArrivalTime;
    private LocalTime plannedDepartureTime;

    private Integer stopSequence;
    private boolean active;
}
