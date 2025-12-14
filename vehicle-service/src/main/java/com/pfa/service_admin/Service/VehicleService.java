package com.pfa.service_admin.Service;


import com.pfa.service_admin.DTO.*;
import com.pfa.service_admin.Entity.Vehicle;

import java.util.List;

public interface VehicleService {

    VehicleResponse createVehicle(CreateVehicleRequest request);

    VehicleResponse getVehicle(Long id);

    List<VehicleResponse> getAllVehicles(Long lineId);

    VehicleResponse updatePosition(Long vehicleId, UpdatePositionRequest request);
    void startTripSimulation(Long vehicleId, StartTripSimulationRequest req);
    void stopTripSimulation(Long vehicleId);

    Vehicle startTrip(Long vehicleId, StartTripRequest req);
}
