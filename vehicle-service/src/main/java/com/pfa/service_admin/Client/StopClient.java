package com.pfa.service_admin.Client;

import com.pfa.service_admin.DTO.StopResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name="schedule-service", contextId="scheduleTripClient")
public interface StopClient {

    @GetMapping("/schedule/stops/{id}")
    StopResponse getStop(@PathVariable Long id);
}
