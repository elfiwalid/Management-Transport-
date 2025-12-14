package com.pfa.service_assurance.DTO;


import lombok.*;
import java.time.LocalTime;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class TripStopItemRequest {
    private Long stopId;
    private Integer sequence;              // stop_sequence
    private LocalTime arrivalTime;         // planned_arrival_time
    private LocalTime departureTime;       // planned_departure_time (nullable)
    private Boolean active;                // nullable => default true
}
