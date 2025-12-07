package com.pfa.service_admin.DTO;


import com.pfa.service_admin.Entity.SignalState;
import com.pfa.service_admin.Entity.VehicleStatus;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class VehicleResponse {

    private Long id;
    private String code;

    private Long lineId;
    private Long driverId;

    private Double latitude;
    private Double longitude;
    private Double speed;
    private Double heading;

    private Instant lastPositionTime;
    private SignalState signalState;
    private VehicleStatus status;

    private Instant createdAt;
    private Instant updatedAt;
}
