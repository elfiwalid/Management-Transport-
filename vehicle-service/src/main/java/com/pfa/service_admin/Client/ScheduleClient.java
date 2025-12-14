package com.pfa.service_admin.Client;

// package com.pfa.service_admin.Client;

import com.pfa.service_admin.DTO.PlannedStopTimeResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.List;

@FeignClient(name = "schedule-service")
public interface ScheduleClient {

    @GetMapping("/schedule/trip-stops")
    List<PlannedStopTimeResponse> getTripStops(
            @RequestParam String tripCode,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    );
}
