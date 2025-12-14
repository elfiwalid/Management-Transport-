package com.pfa.service_assurance.DTO;

import lombok.*;
import java.time.LocalDate;
import java.util.List;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class CreateTripStopsRequest {
    private Long lineId;
    private Long routeId;
    private String tripCode;
    private LocalDate serviceDate;
    private List<TripStopItemRequest> stops;
}
