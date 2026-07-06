package com.example.deliveryservice.Repositories;

import com.example.deliveryservice.Models.Delivery;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

// Interface pra salvar e buscar entregas no MySQL
public interface DeliveryRepository extends JpaRepository<Delivery, Long> {
    Optional<Delivery> findByOrderId(Long orderId);
}
