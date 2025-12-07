package com.pfa.service_sinistre.Service;

import com.pfa.service_sinistre.Client.ScheduleClient;
import com.pfa.service_sinistre.DTO.PlannedStopTimeResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DriverBusinessService {

    private final ScheduleClient scheduleClient;

    public List<PlannedStopTimeResponse> getHorairesPourArret(Long stopId, LocalDate date) {
        // On envoie "2025-12-05" au FeignClient
        return scheduleClient.getScheduleForStop(stopId, date.toString());
    }

    public List<PlannedStopTimeResponse> getHorairesPourLigne(Long lineId, LocalDate date) {
        return scheduleClient.getScheduleForLine(lineId, date.toString());
    }
}
