package com.pfa.service_admin.DTO;

import lombok.Data;

@Data
public class UpdatePositionRequest {
    private Double latitude;
    private Double longitude;
    private Double speed;
    private Double heading;
}
