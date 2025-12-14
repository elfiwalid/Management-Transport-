package com.pfa.service_admin.DTO;

import lombok.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
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
