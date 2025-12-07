package com.pfa.service_sinistre.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pfa.service_sinistre.Service.AlertService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DriverKafkaListener {

    private final ObjectMapper objectMapper;
    private final AlertService alertService;

    @KafkaListener(
            topics = "vehicle.positions",         // ✅ même topic que dans vehicle-service
            groupId = "driver-service-group"
    )
    public void onVehiclePosition(ConsumerRecord<String, String> record) throws Exception {
        String message = record.value();
        log.info("📥 Kafka message reçu de vehicle.positions : {}", message);

        // Désérialisation JSON -> VehiclePositionEvent
        VehiclePositionEvent event =
                objectMapper.readValue(message, VehiclePositionEvent.class);

        log.info("✅ Event désérialisé : {}", event);

        // 👉 Appel de la logique métier
        alertService.handleVehiclePositionEvent(event);
    }
}
