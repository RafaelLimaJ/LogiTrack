package com.example.deliveryservice.Controllers;

import com.example.deliveryservice.Models.Delivery;
import com.example.deliveryservice.Repositories.DeliveryRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// Controller para gerenciar as entregas e disparar status atualizado no Kafka
@RestController
@RequestMapping("/deliveries")
public class DeliveryController {

    private final DeliveryRepository deliveryRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    public DeliveryController(DeliveryRepository deliveryRepository, KafkaTemplate<String, String> kafkaTemplate) {
        this.deliveryRepository = deliveryRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    // Listar todas as entregas
    @GetMapping
    public ResponseEntity<List<Delivery>> getAllDeliveries() {
        return ResponseEntity.ok(deliveryRepository.findAll());
    }

    // Buscar entrega por ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getDeliveryById(@PathVariable Long id) {
        return deliveryRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Atualizar o status da entrega (ex: EM_TRANSITO, ENTREGUE) e mandar pro Kafka
    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateDeliveryStatus(@PathVariable Long id, @RequestParam String status) {
        return deliveryRepository.findById(id)
                .map(delivery -> {
                    delivery.setStatus(status);
                    Delivery updated = deliveryRepository.save(delivery);

                    // Avisa o canal de entregas (deliveries-topic) para o tracking-service
                    String deliveryEvent = String.format("{\"deliveryId\":%d,\"orderId\":%d,\"status\":\"%s\",\"customerName\":\"%s\",\"destinationAddress\":\"%s\"}",
                            updated.getId(), updated.getOrderId(), updated.getStatus(), updated.getCustomerName(), updated.getDestinationAddress());
                    kafkaTemplate.send("deliveries-topic", String.valueOf(updated.getId()), deliveryEvent);

                    return ResponseEntity.ok(updated);
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
