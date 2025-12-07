package com.pfa.service_admin.Service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pfa.service_admin.DTO.CreateVehicleRequest;
import com.pfa.service_admin.DTO.UpdatePositionRequest;
import com.pfa.service_admin.DTO.VehicleResponse;
import com.pfa.service_admin.Entity.SignalState;
import com.pfa.service_admin.Entity.Vehicle;
import com.pfa.service_admin.Entity.VehicleStatus;
import com.pfa.service_admin.Repository.VehicleRepository;
import com.pfa.service_admin.Service.VehicleService;
import com.pfa.service_admin.kafka.VehiclePositionEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class VehicleServiceImpl implements VehicleService {

    private final VehicleRepository vehicleRepository;

    // 🔹 Ajout pour Kafka
    private final KafkaTemplate<String, String> vehicleKafkaTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${app.kafka.topic.vehicle-positions}")
    private String vehiclePositionsTopic;

    @Override
    public VehicleResponse createVehicle(CreateVehicleRequest request) {

        vehicleRepository.findByCode(request.getCode())
                .ifPresent(v -> { throw new RuntimeException("Code véhicule déjà utilisé"); });

        Vehicle vehicle = Vehicle.builder()
                .code(request.getCode())
                .lineId(request.getLineId())
                .driverId(request.getDriverId())
                .status(VehicleStatus.IN_SERVICE)
                .signalState(SignalState.NO_SIGNAL)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        Vehicle saved = vehicleRepository.save(vehicle);
        return toResponse(saved);
    }

    @Override
    public VehicleResponse getVehicle(Long id) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Véhicule introuvable"));
        return toResponse(vehicle);
    }

    @Override
    public List<VehicleResponse> getAllVehicles(Long lineId) {
        List<Vehicle> vehicles =
                lineId != null ? vehicleRepository.findByLineId(lineId) : vehicleRepository.findAll();

        return vehicles.stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public VehicleResponse updatePosition(Long vehicleId, UpdatePositionRequest request) {
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new RuntimeException("Véhicule introuvable"));

        vehicle.setLatitude(request.getLatitude());
        vehicle.setLongitude(request.getLongitude());
        vehicle.setSpeed(request.getSpeed());
        vehicle.setHeading(request.getHeading());
        vehicle.setLastPositionTime(Instant.now());
        vehicle.setSignalState(SignalState.FRESH);
        vehicle.setUpdatedAt(Instant.now());

        Vehicle updated = vehicleRepository.save(vehicle);

        // 🔹 Après la mise à jour : publier l'événement dans Kafka
        publishVehiclePositionEvent(updated);

        return toResponse(updated);
    }

    // --- Mapping Entity -> DTO ---
    private VehicleResponse toResponse(Vehicle v) {
        return VehicleResponse.builder()
                .id(v.getId())
                .code(v.getCode())
                .lineId(v.getLineId())
                .driverId(v.getDriverId())
                .latitude(v.getLatitude())
                .longitude(v.getLongitude())
                .speed(v.getSpeed())
                .heading(v.getHeading())
                .lastPositionTime(v.getLastPositionTime())
                .signalState(v.getSignalState())
                .status(v.getStatus())
                .createdAt(v.getCreatedAt())
                .updatedAt(v.getUpdatedAt())
                .build();
    }

    // 🔹 Nouvelle méthode privée : envoi Kafka
    private void publishVehiclePositionEvent(Vehicle v) {
        try {
            // Pour l’instant on simule un retard de 8 minutes (pour tester driver-service)
            int delayMinutes = 8;

            VehiclePositionEvent event = VehiclePositionEvent.builder()
                    .vehicleId(v.getId())
                    .lineId(v.getLineId())
                    .latitude(v.getLatitude())
                    .longitude(v.getLongitude())
                    .speed(v.getSpeed())
                    .heading(v.getHeading())
                    .delayMinutes(delayMinutes)
                    .build();

            String json = objectMapper.writeValueAsString(event);

            log.info("📤 Envoi événement Kafka vehicle.positions : {}", json);
            vehicleKafkaTemplate.send(vehiclePositionsTopic, json);

        } catch (Exception e) {
            log.error("Erreur lors de l’envoi de l’événement Kafka vehicle.positions", e);
        }
    }
}
