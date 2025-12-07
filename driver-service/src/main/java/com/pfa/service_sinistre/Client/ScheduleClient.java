package com.pfa.service_sinistre.Client;


import com.pfa.service_sinistre.DTO.PlannedStopTimeResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(
        name = "schedule-service"   // DOIT = spring.application.name du microservice horaires
)
public interface ScheduleClient {

    @GetMapping("/schedule/stop/{stopId}")
    List<PlannedStopTimeResponse> getScheduleForStop(
            @PathVariable("stopId") Long stopId,
            @RequestParam("date") String serviceDate // ⬅️ String, plus LocalDate
    );

    @GetMapping("/schedule/line/{lineId}")
    List<PlannedStopTimeResponse> getScheduleForLine(
            @PathVariable("lineId") Long lineId,
            @RequestParam("date") String serviceDate
    );
}
