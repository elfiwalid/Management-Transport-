package com.pfa.service_admin.Controller;

import com.pfa.service_admin.DTO.*;
import com.pfa.service_admin.Entity.Vehicle;
import com.pfa.service_admin.Service.VehicleService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/vehicles")
@RequiredArgsConstructor
public class VehicleController {

    private final VehicleService vehicleService;

    @PostMapping
    public ResponseEntity<VehicleResponse> createVehicle(@RequestBody CreateVehicleRequest request) {
        return ResponseEntity.ok(vehicleService.createVehicle(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<VehicleResponse> getVehicle(@PathVariable Long id) {
        return ResponseEntity.ok(vehicleService.getVehicle(id));
    }

    @GetMapping
    public ResponseEntity<List<VehicleResponse>> getAllVehicles(
            @RequestParam(required = false) Long lineId) {

        return ResponseEntity.ok(vehicleService.getAllVehicles(lineId));
    }

    @PutMapping("/{id}/position")
    public ResponseEntity<VehicleResponse> updatePosition(
            @PathVariable Long id,
            @RequestBody UpdatePositionRequest request) {

        return ResponseEntity.ok(vehicleService.updatePosition(id, request));
    }


    @PostMapping("/{id}/simulate/start")
    public ResponseEntity<String> startSimulation(@PathVariable Long id,
                                                  @RequestBody StartTripSimulationRequest req) {
        vehicleService.startTripSimulation(id, req);
        return ResponseEntity.ok("Simulation started for vehicle " + id);
    }

    @PostMapping("/{id}/simulate/stop")
    public ResponseEntity<String> stopSimulation(@PathVariable Long id) {
        vehicleService.stopTripSimulation(id);
        return ResponseEntity.ok("Simulation stopped for vehicle " + id);
    }

    @PostMapping("/{id}/start-trip")
    public ResponseEntity<Vehicle> startTrip(
            @PathVariable Long id,
            @RequestBody StartTripRequest req
    ) {
        return ResponseEntity.ok(vehicleService.startTrip(id, req));
    }

}
