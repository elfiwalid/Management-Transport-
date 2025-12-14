package com.pfa.service_admin.DTO;

import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class StopResponse {
    private Long id;
    private String name;
    private String code;
    private Double latitude;
    private Double longitude;
}
