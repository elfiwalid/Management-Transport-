package com.pfa.service_assurance.Service;

import com.pfa.service_assurance.DTO.CreatePlannedStopTimeRequest;
import com.pfa.service_assurance.DTO.CreateTripStopsRequest;
import com.pfa.service_assurance.DTO.PlannedStopTimeResponse;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface PlannedStopTimeService {

    PlannedStopTimeResponse create(CreatePlannedStopTimeRequest request);

    PlannedStopTimeResponse getById(Long id);

    List<PlannedStopTimeResponse> getForLine(Long lineId, LocalDate serviceDate);

    List<PlannedStopTimeResponse> getForStop(Long stopId, LocalDate serviceDate);

    PlannedStopTimeResponse update(Long id, CreatePlannedStopTimeRequest request);

    List<PlannedStopTimeResponse> search(Long lineId, Long stopId, LocalDate date);

    List<PlannedStopTimeResponse> getForLineAndStop(Long lineId, Long stopId, LocalDate date);

    List<PlannedStopTimeResponse> createTripStops(CreateTripStopsRequest req);

    List<PlannedStopTimeResponse> getTripStops(String tripCode, LocalDate date);
    List<PlannedStopTimeResponse> getNextDepartures(
            Long lineId,
            Long stopId,
            LocalDate date,
            LocalTime fromTime,
            int limit
    );

    void delete(Long id);

}
