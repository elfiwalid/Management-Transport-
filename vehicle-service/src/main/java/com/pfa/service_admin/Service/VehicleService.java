package com.pfa.service_admin.Service;


import com.pfa.service_admin.DTO.CreateVehicleRequest;
import com.pfa.service_admin.DTO.UpdatePositionRequest;
import com.pfa.service_admin.DTO.VehicleResponse;

import java.util.List;

public interface VehicleService {

    VehicleResponse createVehicle(CreateVehicleRequest request);

    VehicleResponse getVehicle(Long id);

    List<VehicleResponse> getAllVehicles(Long lineId);

    VehicleResponse updatePosition(Long vehicleId, UpdatePositionRequest request);
}
