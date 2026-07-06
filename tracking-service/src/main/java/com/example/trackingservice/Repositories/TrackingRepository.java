package com.example.trackingservice.Repositories;

import com.example.trackingservice.Models.Tracking;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

// Interface pra salvar e buscar rastreamentos no MongoDB (NoSQL)
public interface TrackingRepository extends MongoRepository<Tracking, String> {
    Optional<Tracking> findByDeliveryId(Long deliveryId);
    Optional<Tracking> findByOrderId(Long orderId);
}
