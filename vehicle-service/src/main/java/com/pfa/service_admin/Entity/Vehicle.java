package com.pfa.service_admin.Entity;


import com.pfa.service_admin.Entity.Vehicle;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "vehicles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Code unique du véhicule (ex : "BUS_102")
    @Column(name = "code", nullable = false, unique = true, length = 50)
    private String code;

    // Référence logique vers la ligne (ID du route-service)
    @Column(name = "line_id")
    private Long lineId;

    // Référence logique vers le conducteur (optionnel)
    @Column(name = "driver_id")
    private Long driverId;

    // --- Position actuelle ---

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

    @Column(name = "speed")
    private Double speed;

    @Column(name = "heading")
    private Double heading;

    // Date/heure de la dernière position reçue
    @Column(name = "last_position_time")
    private Instant lastPositionTime;

    // État du signal (facultatif mais utile)
    @Enumerated(EnumType.STRING)
    @Column(name = "signal_state", length = 20)
    private SignalState signalState;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private VehicleStatus status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;


}
