package com.pfa.service_assurance.Service.impl;

import com.pfa.service_assurance.DTO.CreatePlannedStopTimeRequest;
import com.pfa.service_assurance.DTO.PlannedStopTimeResponse;
import com.pfa.service_assurance.Entity.PlannedStopTime;
import com.pfa.service_assurance.Repository.PlannedStopTimeRepository;
import com.pfa.service_assurance.Service.PlannedStopTimeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PlannedStopTimeServiceImpl implements PlannedStopTimeService {

    private final PlannedStopTimeRepository plannedStopTimeRepository;

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
}
