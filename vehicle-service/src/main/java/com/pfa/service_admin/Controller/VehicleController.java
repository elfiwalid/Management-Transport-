package com.pfa.service_admin.Controller;

import com.pfa.service_admin.DTO.CreateVehicleRequest;
import com.pfa.service_admin.DTO.UpdatePositionRequest;
import com.pfa.service_admin.DTO.VehicleResponse;
import com.pfa.service_admin.Service.VehicleService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/vehicles")
@RequiredArgsConstructor
@CrossOrigin
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


}
