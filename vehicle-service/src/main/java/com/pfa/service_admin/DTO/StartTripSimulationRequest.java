package com.pfa.service_admin.DTO;

import lombok.Data;

@Data
public class StartTripSimulationRequest {
    private Long lineId;
    private Double startLat;
    private Double startLon;
    private Double endLat;
    private Double endLon;

    // optionnel
    private Integer plannedDurationMin; // default 50
    private Double trafficFactor;       // default 1.0..1.4
}
