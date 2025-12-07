package com.pfa.service_admin.DTO;

import lombok.Data;

@Data
public class CreateVehicleRequest {
    private String code;
    private Long lineId;
    private Long driverId;
}
