package com.pfa.service_assurance.Service;

import com.pfa.service_assurance.DTO.CreateStopRequest;
import com.pfa.service_assurance.DTO.StopResponse;

import java.util.List;

public interface StopService {
    StopResponse create(CreateStopRequest req);
    StopResponse getById(Long id);
    List<StopResponse> getAll();
    StopResponse update(Long id, CreateStopRequest req);
    void delete(Long id);
}
