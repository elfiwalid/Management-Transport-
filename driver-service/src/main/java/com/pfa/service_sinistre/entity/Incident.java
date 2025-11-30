package com.pfa.service_sinistre.entity;


import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "incidents")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Incident {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "vehicle_id")
    private Long vehicleId;

    @Column(name = "line_id")
    private Long lineId;

    @Column(name = "trip_id")
    private String tripId;

    @Column(name = "stop_id")
    private Long stopId;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 50)
    private IncidentType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "severity", nullable = false, length = 20)
    private IncidentSeverity severity;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private IncidentStatus status;

    @Column(name = "description", length = 1000)
    private String description;

    // Qui a déclaré (driver / opérateur / user id)
    @Column(name = "reported_by", length = 100)
    private String reportedBy;

    @Column(name = "reported_at", nullable = false)
    private Instant reportedAt;



}
