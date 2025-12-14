package com.pfa.service_assurance.DTO;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StopResponse {
    private Long id;
    private String name;
    private Double latitude;
    private Double longitude;
    private String code;
}
