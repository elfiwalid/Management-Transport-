package com.pfa.service_assurance.Service.impl;

import com.pfa.service_assurance.DTO.CreatePlannedStopTimeRequest;
import com.pfa.service_assurance.DTO.CreateTripStopsRequest;
import com.pfa.service_assurance.DTO.PlannedStopTimeResponse;
import com.pfa.service_assurance.Entity.PlannedStopTime;
import com.pfa.service_assurance.Repository.PlannedStopTimeRepository;
import com.pfa.service_assurance.Repository.StopRepository;
import com.pfa.service_assurance.Service.PlannedStopTimeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PlannedStopTimeServiceImpl implements PlannedStopTimeService {

    private final PlannedStopTimeRepository plannedStopTimeRepository;
    private final StopRepository stopRepository;

    @Override
    public PlannedStopTimeResponse create(CreatePlannedStopTimeRequest request) {
        PlannedStopTime entity = PlannedStopTime.builder()
                .lineId(request.getLineId())
                .routeId(request.getRouteId())
                .tripCode(request.getTripCode())
                .stopId(request.getStopId())
                .serviceDate(request.getServiceDate())
                .plannedArrivalTime(request.getPlannedArrivalTime())
                .plannedDepartureTime(request.getPlannedDepartureTime())
                .stopSequence(request.getStopSequence())
                .active(request.isActive())
                .build();

        PlannedStopTime saved = plannedStopTimeRepository.save(entity);
        return toResponse(saved);
    }

    @Override
    public PlannedStopTimeResponse getById(Long id) {
        PlannedStopTime entity = plannedStopTimeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Horaire introuvable"));
        return toResponse(entity);
    }

    @Override
    public List<PlannedStopTimeResponse> getForLine(Long lineId, LocalDate serviceDate) {
        List<PlannedStopTime> list =
                plannedStopTimeRepository.findByLineIdAndServiceDateOrderByTripCodeAscStopSequenceAsc(
                        lineId, serviceDate
                );
        return list.stream().map(this::toResponse).toList();
    }

    @Override
    public List<PlannedStopTimeResponse> getForStop(Long stopId, LocalDate serviceDate) {
        List<PlannedStopTime> list =
                plannedStopTimeRepository.findByStopIdAndServiceDateOrderByPlannedArrivalTimeAsc(
                        stopId, serviceDate
                );
        return list.stream().map(this::toResponse).toList();
    }

    @Override
    public PlannedStopTimeResponse update(Long id, CreatePlannedStopTimeRequest request) {
        PlannedStopTime entity = plannedStopTimeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Horaire introuvable"));

        entity.setLineId(request.getLineId());
        entity.setRouteId(request.getRouteId());
        entity.setTripCode(request.getTripCode());
        entity.setStopId(request.getStopId());
        entity.setServiceDate(request.getServiceDate());
        entity.setPlannedArrivalTime(request.getPlannedArrivalTime());
        entity.setPlannedDepartureTime(request.getPlannedDepartureTime());
        entity.setStopSequence(request.getStopSequence());
        entity.setActive(request.isActive());

        PlannedStopTime updated = plannedStopTimeRepository.save(entity);
        return toResponse(updated);
    }







    @Override
    public void delete(Long id) {
        if (!plannedStopTimeRepository.existsById(id)) {
            throw new RuntimeException("Horaire introuvable");
        }
        plannedStopTimeRepository.deleteById(id);
    }

    // --- mapping entity -> DTO ---
    private PlannedStopTimeResponse toResponse(PlannedStopTime e) {
        return PlannedStopTimeResponse.builder()
                .id(e.getId())
                .lineId(e.getLineId())
                .routeId(e.getRouteId())
                .tripCode(e.getTripCode())
                .stopId(e.getStopId())
                .serviceDate(e.getServiceDate())
                .plannedArrivalTime(e.getPlannedArrivalTime())
                .plannedDepartureTime(e.getPlannedDepartureTime())
                .stopSequence(e.getStopSequence())
                .active(e.isActive())
                .build();
    }



    @Override
    public List<PlannedStopTimeResponse> search(Long lineId, Long stopId, LocalDate date) {

        // Cas 1: pas de filtre -> tout (à éviter si bdd énorme, mais OK pour mini projet)
        if (lineId == null && stopId == null && date == null) {
            return plannedStopTimeRepository.findAll().stream().map(this::toResponse).toList();
        }

        // Cas 2: date obligatoire pour les endpoints front-friendly (recommandé)
        // si tu veux forcer date :
        // if (date == null) throw new RuntimeException("date est obligatoire");

        List<PlannedStopTime> list;

        if (lineId != null && stopId != null && date != null) {
            list = plannedStopTimeRepository
                    .findByLineIdAndStopIdAndServiceDateOrderByPlannedArrivalTimeAsc(lineId, stopId, date);
        } else if (lineId != null && date != null) {
            list = plannedStopTimeRepository
                    .findByLineIdAndServiceDateOrderByPlannedArrivalTimeAsc(lineId, date);
        } else if (stopId != null && date != null) {
            list = plannedStopTimeRepository
                    .findByStopIdAndServiceDateOrderByPlannedArrivalTimeAsc(stopId, date);
        } else {
            // fallback simple si date manquante
            list = plannedStopTimeRepository.findAll().stream()
                    .filter(x -> lineId == null || x.getLineId().equals(lineId))
                    .filter(x -> stopId == null || x.getStopId().equals(stopId))
                    .filter(x -> date == null || x.getServiceDate().equals(date))
                    .toList();
        }

        return list.stream().map(this::toResponse).toList();
    }

    @Override
    public List<PlannedStopTimeResponse> getForLineAndStop(Long lineId, Long stopId, LocalDate date) {
        List<PlannedStopTime> list =
                plannedStopTimeRepository.findByLineIdAndStopIdAndServiceDateOrderByPlannedArrivalTimeAsc(lineId, stopId, date);
        return list.stream().map(this::toResponse).toList();
    }

    @Override
    public List<PlannedStopTimeResponse> getNextDepartures(Long lineId, Long stopId, LocalDate date, LocalTime fromTime, int limit) {

        // 1) essayer plannedDepartureTime
        List<PlannedStopTime> list = plannedStopTimeRepository
                .findByLineIdAndStopIdAndServiceDateAndPlannedDepartureTimeGreaterThanEqualOrderByPlannedDepartureTimeAsc(
                        lineId, stopId, date, fromTime, PageRequest.of(0, limit)
                );

        // 2) si pas de departureTime (null partout) -> fallback arrivalTime
        if (list == null || list.isEmpty()) {
            list = plannedStopTimeRepository
                    .findByLineIdAndStopIdAndServiceDateAndPlannedArrivalTimeGreaterThanEqualOrderByPlannedArrivalTimeAsc(
                            lineId, stopId, date, fromTime, PageRequest.of(0, limit)
                    );
        }

        return list.stream().map(this::toResponse).toList();
    }


    public List<PlannedStopTimeResponse> createTripStops(CreateTripStopsRequest req) {
        // validations basiques
        if (req.getLineId() == null || req.getRouteId() == null || req.getServiceDate() == null
                || req.getTripCode() == null || req.getTripCode().isBlank()
                || req.getStops() == null || req.getStops().isEmpty()) {
            throw new IllegalArgumentException("lineId, routeId, tripCode, serviceDate, stops sont obligatoires");
        }

        // 1) vérifier stopIds existent
        // (simple: StopRepository.existsById pour chaque stopId)
        for (var s : req.getStops()) {
            if (s.getStopId() == null || s.getSequence() == null || s.getArrivalTime() == null) {
                throw new IllegalArgumentException("Chaque stop doit avoir stopId, sequence, arrivalTime");
            }
            if (!stopRepository.existsById(s.getStopId())) {
                throw new IllegalArgumentException("StopId invalide: " + s.getStopId());
            }
        }

        // 2) OPTIONAL: supprimer les anciens stops du même trip/date si tu veux “replace”
        // plannedStopTimeRepository.deleteByTripCodeAndServiceDate(req.getTripCode(), req.getServiceDate());

        // 3) créer plusieurs rows
        List<PlannedStopTime> entities = req.getStops().stream().map(s ->
                PlannedStopTime.builder()
                        .lineId(req.getLineId())
                        .routeId(req.getRouteId())
                        .tripCode(req.getTripCode())
                        .stopId(s.getStopId())
                        .serviceDate(req.getServiceDate())
                        .plannedArrivalTime(s.getArrivalTime())
                        .plannedDepartureTime(s.getDepartureTime())
                        .stopSequence(s.getSequence())
                        .active(s.getActive() == null ? true : s.getActive())
                        .build()
        ).toList();

        List<PlannedStopTime> saved = plannedStopTimeRepository.saveAll(entities);

        // 4) retourner responses
        return saved.stream().map(this::toResponse).toList();
    }

    @Override
    public List<PlannedStopTimeResponse> getTripStops(String tripCode, LocalDate date) {
        if (tripCode == null || tripCode.isBlank() || date == null) {
            throw new IllegalArgumentException("tripCode et date sont obligatoires");
        }

        return plannedStopTimeRepository
                .findByTripCodeAndServiceDateOrderByStopSequenceAsc(tripCode, date)
                .stream()
                .map(this::toResponse)
                .toList();
    }



}
