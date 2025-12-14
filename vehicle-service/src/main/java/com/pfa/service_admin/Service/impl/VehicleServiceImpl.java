package com.pfa.service_admin.Service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pfa.service_admin.Client.ScheduleClient;
import com.pfa.service_admin.Client.StopClient;
import com.pfa.service_admin.DTO.*;
import com.pfa.service_admin.Entity.SignalState;
import com.pfa.service_admin.Entity.Vehicle;
import com.pfa.service_admin.Entity.VehicleStatus;
import com.pfa.service_admin.Repository.VehicleRepository;
import com.pfa.service_admin.Service.VehicleService;
import com.pfa.service_admin.kafka.VehiclePositionEvent;
import com.pfa.service_admin.simulation.TripSimulationState;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class VehicleServiceImpl implements VehicleService {

    private final VehicleRepository vehicleRepository;
    private final ConcurrentMap<Long, TripSimulationState> simulations = new ConcurrentHashMap<>();
    private final StopClient stopClient;
    private final ScheduleClient scheduleClient;
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

    @Override
    public void startTripSimulation(Long vehicleId, StartTripSimulationRequest req) {
        Vehicle v = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new RuntimeException("Véhicule introuvable"));

        int planned = (req.getPlannedDurationMin() != null) ? req.getPlannedDurationMin() : 50;
        double traffic = (req.getTrafficFactor() != null) ? req.getTrafficFactor() : 1.15; // petit retard réaliste

        simulations.put(vehicleId, TripSimulationState.builder()
                .vehicleId(vehicleId)
                .lineId(req.getLineId() != null ? req.getLineId() : v.getLineId())
                .startLat(req.getStartLat())
                .startLon(req.getStartLon())
                .endLat(req.getEndLat())
                .endLon(req.getEndLon())
                .startedAt(Instant.now())
                .plannedDurationMin(planned)
                .trafficFactor(traffic)
                .finished(false)
                .build());
    }

    @Override
    public void stopTripSimulation(Long vehicleId) {
        simulations.remove(vehicleId);
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
                // ✅ trip mapping
                .currentTripCode(v.getCurrentTripCode())
                .currentServiceDate(v.getCurrentServiceDate())
                .tripStartedAt(v.getTripStartedAt())
                .tripStatus(v.getTripStatus())
                .delayMinutes(v.getDelayMinutes())
                .createdAt(v.getCreatedAt())
                .updatedAt(v.getUpdatedAt())
                .build();
    }

    // 🔹 Nouvelle méthode privée : envoi Kafka
    private void publishVehiclePositionEvent(Vehicle v) {
        try {
            // Pour l’instant on simule un retard de 8 minutes (pour tester driver-service)
            int delayMinutes = v.getDelayMinutes() != null ? v.getDelayMinutes() : 0;


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


    @Override
    public Vehicle startTrip(Long vehicleId, StartTripRequest req) {

        if (req.getTripCode() == null || req.getTripCode().isBlank() || req.getServiceDate() == null) {
            throw new IllegalArgumentException("tripCode et serviceDate sont obligatoires");
        }

        Vehicle v = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new RuntimeException("Vehicle introuvable"));

        // Empêcher 2 trips en même temps
        if ("RUNNING".equalsIgnoreCase(v.getTripStatus())) {
            throw new IllegalStateException("Ce véhicule a déjà un trip RUNNING");
        }

        // ✅ Vérifier que le trip existe dans schedule-service
        List<PlannedStopTimeResponse> stops = scheduleClient.getTripStops(req.getTripCode(), req.getServiceDate());
        if (stops == null || stops.isEmpty()) {
            throw new IllegalArgumentException("Trip introuvable dans schedule-service: " + req.getTripCode());
        }

        // ✅ Lier le trip au véhicule
        v.setCurrentTripCode(req.getTripCode());
        v.setCurrentServiceDate(req.getServiceDate());
        v.setTripStartedAt(Instant.now());
        v.setTripStatus("RUNNING");

        // Optionnel : status véhicule
        v.setStatus(VehicleStatus.IN_SERVICE); // si tu l’as

        v.setUpdatedAt(Instant.now());
        return vehicleRepository.save(v);
    }



    private PlannedStopTimeResponse findExpectedStop(List<PlannedStopTimeResponse> stops) {
        if (stops == null || stops.isEmpty()) return null;

        // stops déjà triés normalement, sinon :
        stops = stops.stream()
                .sorted((a,b) -> Integer.compare(
                        a.getStopSequence() == null ? 0 : a.getStopSequence(),
                        b.getStopSequence() == null ? 0 : b.getStopSequence()
                ))
                .toList();

        var now = java.time.LocalTime.now();

        PlannedStopTimeResponse expected = stops.get(0);
        for (PlannedStopTimeResponse s : stops) {
            if (s.getPlannedArrivalTime() != null && !s.getPlannedArrivalTime().isAfter(now)) {
                expected = s;
            }
        }
        return expected;
    }

    private int simulateDelayMinutes() {
        // retard réaliste 0..9
        return (int) (Math.random() * 10);
    }

    @Scheduled(fixedRate = 10000) // 10 s
    public void autoUpdateRunningTrips() {

        List<Vehicle> runningVehicles =
                vehicleRepository.findByTripStatusIgnoreCase("RUNNING");

        if (runningVehicles.isEmpty()) return;

        for (Vehicle v : runningVehicles) {
            try {
                if (v.getCurrentTripCode() == null || v.getCurrentServiceDate() == null) continue;

                // 1) récupérer le planning du trip
                List<PlannedStopTimeResponse> tripStops =
                        scheduleClient.getTripStops(v.getCurrentTripCode(), v.getCurrentServiceDate());

                if (tripStops == null || tripStops.isEmpty()) continue;

                // 2) stop attendu maintenant
                PlannedStopTimeResponse expected = findExpectedStop(tripStops);
                if (expected == null) continue;

                // 3) récupérer coords du stop (via stop-service chez schedule? ou local ?)
                // 👉 IMPORTANT :
                // ton schedule-service stocke stopId, mais vehicle-service n'a pas la latitude/longitude du stop.
                // SOLUTION SIMPLE POUR TON MINI-PROJET :
                // - soit tu ajoutes stop.lat/stop.lon dans schedule-service response
                // - soit tu fais un StopClient vers schedule-service /schedule/stops/{id}
                // On va faire le StopClient (propre).

                StopResponse stop = stopClient.getStop(expected.getStopId());

                // 4) simuler retard
                int delay = simulateDelayMinutes();

                // 5) update DB vehicle
                v.setLatitude(stop.getLatitude());
                v.setLongitude(stop.getLongitude());
                v.setSpeed(20 + Math.random() * 20); // 20..40
                v.setHeading(0 + Math.random() * 360);
                v.setLastPositionTime(Instant.now());
                v.setSignalState(SignalState.FRESH);
                v.setDelayMinutes(delay);
                v.setUpdatedAt(Instant.now());

                Vehicle updated = vehicleRepository.save(v);

                // 6) Kafka event
                publishVehiclePositionEvent(updated);

                // 7) fin du trip si dernier stop atteint (optionnel maintenant)
                Integer maxSeq = tripStops.stream()
                        .map(PlannedStopTimeResponse::getStopSequence)
                        .filter(x -> x != null)
                        .max(Integer::compareTo)
                        .orElse(null);

                if (maxSeq != null && expected.getStopSequence() != null
                        && expected.getStopSequence().equals(maxSeq)) {
                    // arrivé dernier stop
                    updated.setTripStatus("FINISHED");
                    updated.setUpdatedAt(Instant.now());
                    vehicleRepository.save(updated);
                }

            } catch (Exception e) {
                log.error("Erreur scheduler vehicle RUNNING id=" + v.getId(), e);
            }
        }
    }



}
