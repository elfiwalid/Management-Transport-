package com.pfa.service_admin.controller;

import com.pfa.service_admin.dto.DashboardDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/admin")
@CrossOrigin(origins = "*")
public class AdminController {

    @Autowired
    private RestTemplate restTemplate;

    private static final String SERVICE_ASSURANCE_URL = "http://localhost:8091";
    private static final String SERVICE_SINISTRE_URL = "http://localhost:8094";
    private static final String SERVICE_AUTH_URL = "http://localhost:8093";

    @GetMapping("/dashboard")
    public ResponseEntity<DashboardDTO> getDashboard() {
        try {
            // Récupérer les statistiques des différents services
            Long totalClients = getTotalClients();
            Map<String, Long> statsSinistres = getStatistiquesSinistres();

            DashboardDTO dashboard = new DashboardDTO(
                    totalClients,
                    statsSinistres.get("total"),
                    statsSinistres.get("enAttente"),
                    statsSinistres.get("valides"),
                    "Services opérationnels"
            );

            return ResponseEntity.ok(dashboard);
        } catch (Exception e) {
            DashboardDTO errorDashboard = new DashboardDTO(0L, 0L, 0L, 0L, "Erreur de communication avec les services");
            return ResponseEntity.ok(errorDashboard);
        }
    }

    @GetMapping("/statistiques")
    public ResponseEntity<Map<String, Object>> getStatistiques() {
        Map<String, Object> stats = new HashMap<>();

        try {
            stats.put("totalClients", getTotalClients());
            stats.put("statistiquesSinistres", getStatistiquesSinistres());
            stats.put("servicesStatus", getServicesStatus());
            stats.put("timestamp", System.currentTimeMillis());
        } catch (Exception e) {
            stats.put("error", "Erreur lors de la récupération des statistiques");
            stats.put("details", e.getMessage());
        }

        return ResponseEntity.ok(stats);
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Service Admin is running");
    }

    @GetMapping("/services/status")
    public ResponseEntity<Map<String, String>> getServicesStatus() {
        Map<String, String> status = new HashMap<>();

        // Vérifier le service assurance
        try {
            ResponseEntity<String> response = restTemplate.getForEntity(SERVICE_ASSURANCE_URL + "/clients/health", String.class);
            status.put("service-assurance", response.getStatusCode().is2xxSuccessful() ? "UP" : "DOWN");
        } catch (Exception e) {
            status.put("service-assurance", "DOWN");
        }

        // Vérifier le service sinistre
        try {
            ResponseEntity<String> response = restTemplate.getForEntity(SERVICE_SINISTRE_URL + "/sinistres/health", String.class);
            status.put("service-sinistre", response.getStatusCode().is2xxSuccessful() ? "UP" : "DOWN");
        } catch (Exception e) {
            status.put("service-sinistre", "DOWN");
        }

        // Vérifier le service auth
        try {
            ResponseEntity<String> response = restTemplate.getForEntity(SERVICE_AUTH_URL + "/auth/health", String.class);
            status.put("service-auth", response.getStatusCode().is2xxSuccessful() ? "UP" : "DOWN");
        } catch (Exception e) {
            status.put("service-auth", "DOWN");
        }

        return ResponseEntity.ok(status);
    }

    // Méthodes privées pour récupérer les données via REST
    private Long getTotalClients() {
        try {
            String url = SERVICE_ASSURANCE_URL + "/clients";
            Object[] clients = restTemplate.getForObject(url, Object[].class);
            return clients != null ? (long) clients.length : 0L;
        } catch (Exception e) {
            return 0L;
        }
    }

    private Map<String, Long> getStatistiquesSinistres() {
        Map<String, Long> stats = new HashMap<>();
        stats.put("total", 0L);
        stats.put("enAttente", 0L);
        stats.put("valides", 0L);

        try {
            String url = SERVICE_SINISTRE_URL + "/sinistres";
            Object[] sinistres = restTemplate.getForObject(url, Object[].class);

            if (sinistres != null) {
                stats.put("total", (long) sinistres.length);
                // Pour une version simple, on simule des statistiques
                stats.put("enAttente", (long) (sinistres.length * 0.3)); // 30% en attente
                stats.put("valides", (long) (sinistres.length * 0.5));   // 50% validés
            }
        } catch (Exception e) {
            // Garde les valeurs par défaut (0)
        }

        return stats;
    }
}