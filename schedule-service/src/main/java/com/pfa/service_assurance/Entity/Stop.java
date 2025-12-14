package com.pfa.service_assurance.Entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "stops")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Stop {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="name", nullable=false, length=120)
    private String name;

    @Column(name="latitude", nullable=false)
    private Double latitude;

    @Column(name="longitude", nullable=false)
    private Double longitude;

    @Column(name="code", length=50)
    private String code; // optionnel ex: "DONOR", "HASSAN2"
}
