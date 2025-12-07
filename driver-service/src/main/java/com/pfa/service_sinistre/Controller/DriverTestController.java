package com.pfa.service_sinistre.Controller;

import com.pfa.service_sinistre.DTO.PlannedStopTimeResponse;
import com.pfa.service_sinistre.Service.DriverBusinessService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/driver")
@RequiredArgsConstructor
@CrossOrigin
public class DriverTestController {

    private final DriverBusinessService driverBusinessService;

    // GET /driver/schedule/stop/100?date=2025-12-05
    @GetMapping("/schedule/stop/{stopId}")
    public ResponseEntity<List<PlannedStopTimeResponse>> getHorairesPourArret(
            @PathVariable Long stopId,
            @RequestParam("date")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        return ResponseEntity.ok(driverBusinessService.getHorairesPourArret(stopId, date));
    }

    // GET /driver/schedule/line/1?date=2025-12-05
    @GetMapping("/schedule/line/{lineId}")
    public ResponseEntity<List<PlannedStopTimeResponse>> getHorairesPourLigne(
            @PathVariable Long lineId,
            @RequestParam("date")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        return ResponseEntity.ok(driverBusinessService.getHorairesPourLigne(lineId, date));
    }
}
