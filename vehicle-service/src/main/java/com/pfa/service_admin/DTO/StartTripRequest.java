package com.pfa.service_admin.DTO;

import lombok.*;
import java.time.LocalDate;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class StartTripRequest {
    private String tripCode;
    private LocalDate serviceDate;
}
