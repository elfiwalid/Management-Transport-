package com.pfa.service_admin.simulation;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class TripSimulationState {
    private Long vehicleId;
    private Long lineId;

    private double startLat;
    private double startLon;
    private double endLat;
    private double endLon;

    private Instant startedAt;

    // durée normale en minutes (ex: 50)
    private int plannedDurationMin;

    // facteur trafic (ex: 1.0 normal, 1.2 plus lent)
    private double trafficFactor;

    private boolean finished;
}
