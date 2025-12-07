package com.pfa.service_assurance.Service;

import com.pfa.service_assurance.DTO.CreatePlannedStopTimeRequest;
import com.pfa.service_assurance.DTO.PlannedStopTimeResponse;

import java.time.LocalDate;
import java.util.List;

public interface PlannedStopTimeService {

    PlannedStopTimeResponse create(CreatePlannedStopTimeRequest request);

    PlannedStopTimeResponse getById(Long id);

    List<PlannedStopTimeResponse> getForLine(Long lineId, LocalDate serviceDate);

    List<PlannedStopTimeResponse> getForStop(Long stopId, LocalDate serviceDate);

    PlannedStopTimeResponse update(Long id, CreatePlannedStopTimeRequest request);

    void delete(Long id);
}
