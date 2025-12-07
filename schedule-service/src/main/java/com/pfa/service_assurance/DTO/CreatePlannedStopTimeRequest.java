package com.pfa.service_assurance.DTO;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class CreatePlannedStopTimeRequest {

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
