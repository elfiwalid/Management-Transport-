package com.pfa.service_sinistre.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "sinistres")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Sinistre {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String numeroSinistre;

    @Column(nullable = false)
    private String description;

    @Column(name = "date_declaration")
    private LocalDateTime dateDeclaration = LocalDateTime.now();

    @Column(name = "montant_demande")
    private Double montantDemande;

    @Enumerated(EnumType.STRING)
    private StatutSinistre statut = StatutSinistre.DECLARE;

    @Column(name = "client_id", nullable = false)
    private Long clientId;

    // Champ pour stocker les infos du client (récupérées via REST)
    @Transient
    private String clientNom;

    @Transient
    private String clientEmail;
}