package com.pfa.service_sinistre.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "alerts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Alert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 30)
    private AlertType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "severity", nullable = false, length = 20)
    private AlertSeverity severity;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private AlertStatus status;

    // Liens logiques
    @Column(name = "vehicle_id")
    private Long vehicleId;

    @Column(name = "line_id")
    private Long lineId;

    @Column(name = "trip_id")
    private String tripId;

    @Column(name = "stop_id")
    private Long stopId;

    // Délai en minutes si c'est une alerte de retard
    @Column(name = "delay_minutes")
    private Integer delayMinutes;

    // Référence éventuelle à un Incident
    @Column(name = "incident_id")
    private Long incidentId;

    // Texte court pour UI
    @Column(name = "title", length = 255)
    private String title;

    @Column(name = "details", length = 2000)
    private String details;

    // Cycle de vie
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;


}
