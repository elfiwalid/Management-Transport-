package com.pfa.service_sinistre.Controller;


import com.pfa.service_sinistre.entity.Alert;
import com.pfa.service_sinistre.Service.AlertService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/driver/alerts")
@RequiredArgsConstructor
public class AlertController {

    private final AlertService alertService;

    @GetMapping("/open")
    public List<Alert> getOpenAlerts() {
        return alertService.getOpenAlerts();
    }

    @GetMapping("/vehicle/{vehicleId}")
    public List<Alert> getAlertsForVehicle(@PathVariable Long vehicleId) {
        return alertService.getAlertsForVehicle(vehicleId);
    }

    @PutMapping("/{id}/status")
    public Alert updateStatus(@PathVariable Long id, @RequestParam String status) {
        return alertService.updateStatus(id, status);
    }
}
