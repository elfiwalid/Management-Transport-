package com.pfa.service_assurance.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "planned_stop_times")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlannedStopTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Références vers les autres services (ids simples, pas de relation JPA)
    @Column(name = "line_id", nullable = false)
    private Long lineId;      // Ligne (route-service)

    @Column(name = "route_id", nullable = false)
    private Long routeId;     // Route/sens (route-service)

    @Column(name = "trip_code", length = 100)
    private String tripCode;  // Identifiant logique du trajet (ex : "L1_08H30_A")

    @Column(name = "stop_id", nullable = false)
    private Long stopId;      // Arrêt (route-service)

    // Date de service (jour où ce passage est prévu)
    @Column(name = "service_date", nullable = false)
    private LocalDate serviceDate;

    // Heure planifiée pour cet arrêt
    @Column(name = "planned_arrival_time", nullable = false)
    private LocalTime plannedArrivalTime;

    // Optionnel : heure de départ (si différente)
    @Column(name = "planned_departure_time")
    private LocalTime plannedDepartureTime;

    @Column(name = "stop_sequence")
    private Integer stopSequence;

    @Column(name = "active", nullable = false)
    private boolean active;
}
