package com.pfa.service_sinistre.kafka;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
// Très important : ignore les champs en plus si jamais on évolue le JSON
@JsonIgnoreProperties(ignoreUnknown = true)
public class VehiclePositionEvent {

    private Long vehicleId;
    private Long lineId;
    private Double latitude;
    private Double longitude;
    private Double speed;
    private Double heading;
    private Integer delayMinutes;
}
