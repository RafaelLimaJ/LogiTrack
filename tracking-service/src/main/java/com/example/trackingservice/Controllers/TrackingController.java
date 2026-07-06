package com.example.trackingservice.Controllers;

import com.example.trackingservice.Models.Tracking;
import com.example.trackingservice.Repositories.TrackingRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// Controller para consultar o rastreamento dos pedidos e entregas
@RestController
@RequestMapping("/tracking")
public class TrackingController {

    private final TrackingRepository trackingRepository;

    public TrackingController(TrackingRepository trackingRepository) {
        this.trackingRepository = trackingRepository;
    }

    // Consulta rastreamento pelo ID da entrega
    @GetMapping("/delivery/{deliveryId}")
    public ResponseEntity<?> getTrackingByDeliveryId(@PathVariable Long deliveryId) {
        return trackingRepository.findByDeliveryId(deliveryId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Consulta rastreamento pelo ID do pedido original
    @GetMapping("/order/{orderId}")
    public ResponseEntity<?> getTrackingByOrderId(@PathVariable Long orderId) {
        return trackingRepository.findByOrderId(orderId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
