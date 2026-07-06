package com.example.orderservice.Controllers;

import com.example.orderservice.Models.Order;
import com.example.orderservice.Repositories.OrderRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// Controller que gerencia as ordens de serviço (pedidos)
@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderRepository orderRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    public OrderController(OrderRepository orderRepository, KafkaTemplate<String, String> kafkaTemplate) {
        this.orderRepository = orderRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    // Listar todos os pedidos
    @GetMapping
    public ResponseEntity<List<Order>> getAllOrders() {
        return ResponseEntity.ok(orderRepository.findAll());
    }

    // Buscar pedido pelo ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getOrderById(@PathVariable Long id) {
        return orderRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Criar um novo pedido e avisar o Kafka pra disparar a entrega
    @PostMapping
    public ResponseEntity<?> createOrder(@RequestBody Order order) {
        order.setStatus("PENDENTE");
        Order savedOrder = orderRepository.save(order);

        // Monta o JSON em String de um jeito super simples
        String event = String.format("{\"orderId\":%d,\"status\":\"%s\",\"customerName\":\"%s\",\"destinationAddress\":\"%s\"}",
                savedOrder.getId(), savedOrder.getStatus(), savedOrder.getCustomerName(), savedOrder.getDestinationAddress());

        // Dispara o evento pro Kafka no canal 'orders-topic'
        kafkaTemplate.send("orders-topic", String.valueOf(savedOrder.getId()), event);

        return ResponseEntity.ok(savedOrder);
    }

    // Atualizar o status do pedido (ex: aprovado, cancelado)
    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateOrderStatus(@PathVariable Long id, @RequestParam String status) {
        return orderRepository.findById(id)
                .map(order -> {
                    order.setStatus(status);
                    Order updated = orderRepository.save(order);

                    // Avisa o Kafka sobre a mudança de status do pedido
                    String event = String.format("{\"orderId\":%d,\"status\":\"%s\",\"customerName\":\"%s\",\"destinationAddress\":\"%s\"}",
                            updated.getId(), updated.getStatus(), updated.getCustomerName(), updated.getDestinationAddress());
                    kafkaTemplate.send("orders-topic", String.valueOf(updated.getId()), event);

                    return ResponseEntity.ok(updated);
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
