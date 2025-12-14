package com.pfa.service_assurance.DTO;
import lombok.Data;

@Data
public class CreateStopRequest {
    private String name;
    private Double latitude;
    private Double longitude;
    private String code;
}
