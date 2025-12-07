package com.pfa.service_admin.kafka;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class VehiclePositionEvent {

    private Long vehicleId;
    private Long lineId;
    private Double latitude;
    private Double longitude;
    private Double speed;
    private Double heading;

    // Pour l’instant, on peut simuler un retard (ou mettre 0)
    private Integer delayMinutes;
}
