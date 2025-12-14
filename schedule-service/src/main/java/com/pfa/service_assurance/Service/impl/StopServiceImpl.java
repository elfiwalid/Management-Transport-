package com.pfa.service_assurance.Service.impl;

import com.pfa.service_assurance.DTO.CreateStopRequest;
import com.pfa.service_assurance.DTO.StopResponse;
import com.pfa.service_assurance.Entity.Stop;
import com.pfa.service_assurance.Repository.StopRepository;
import com.pfa.service_assurance.Service.StopService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StopServiceImpl implements StopService {

    private final StopRepository stopRepository;

    @Override
    public StopResponse create(CreateStopRequest req) {
        Stop s = Stop.builder()
                .name(req.getName())
                .latitude(req.getLatitude())
                .longitude(req.getLongitude())
                .code(req.getCode())
                .build();
        return toResponse(stopRepository.save(s));
    }

    @Override
    public StopResponse getById(Long id) {
        Stop s = stopRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Stop introuvable"));
        return toResponse(s);
    }

    @Override
    public List<StopResponse> getAll() {
        return stopRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Override
    public StopResponse update(Long id, CreateStopRequest req) {
        Stop s = stopRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Stop introuvable"));
        s.setName(req.getName());
        s.setLatitude(req.getLatitude());
        s.setLongitude(req.getLongitude());
        s.setCode(req.getCode());
        return toResponse(stopRepository.save(s));
    }

    @Override
    public void delete(Long id) {
        stopRepository.deleteById(id);
    }

    private StopResponse toResponse(Stop s) {
        return StopResponse.builder()
                .id(s.getId())
                .name(s.getName())
                .latitude(s.getLatitude())
                .longitude(s.getLongitude())
                .code(s.getCode())
                .build();
    }
}
