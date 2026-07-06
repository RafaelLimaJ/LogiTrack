package com.example.deliveryservice.Kafka;

import com.example.deliveryservice.Models.Delivery;
import com.example.deliveryservice.Repositories.DeliveryRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

// Classe que escuta eventos do Kafka vindos do order-service
@Service
public class OrderConsumer {

    private final DeliveryRepository deliveryRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public OrderConsumer(DeliveryRepository deliveryRepository, KafkaTemplate<String, String> kafkaTemplate) {
        this.deliveryRepository = deliveryRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    // Fica de olho no canal de pedidos (orders-topic)
    @KafkaListener(topics = "orders-topic", groupId = "delivery-group")
    public void consumeOrderEvent(String eventMessage) {
        try {
            // Transforma o texto JSON em objeto para ler os campos
            JsonNode jsonNode = objectMapper.readTree(eventMessage);
            Long orderId = jsonNode.get("orderId").asLong();
            String status = jsonNode.get("status").asText();
            String customerName = jsonNode.get("customerName").asText();
            String destinationAddress = jsonNode.get("destinationAddress").asText();

            // Se o pedido foi criado (PENDENTE), a gente cria a entrega dele
            if ("PENDENTE".equals(status)) {
                // Se a entrega já não existir, cria uma nova
                if (deliveryRepository.findByOrderId(orderId).isEmpty()) {
                    Delivery delivery = new Delivery();
                    delivery.setOrderId(orderId);
                    delivery.setStatus("CRIADO");
                    delivery.setCustomerName(customerName);
                    delivery.setDestinationAddress(destinationAddress);
                    Delivery saved = deliveryRepository.save(delivery);

                    // Avisa o canal de entregas (deliveries-topic) que uma entrega foi gerada
                    String deliveryEvent = String.format("{\"deliveryId\":%d,\"orderId\":%d,\"status\":\"%s\",\"customerName\":\"%s\",\"destinationAddress\":\"%s\"}",
                            saved.getId(), saved.getOrderId(), saved.getStatus(), saved.getCustomerName(), saved.getDestinationAddress());
                    kafkaTemplate.send("deliveries-topic", String.valueOf(saved.getId()), deliveryEvent);
                }
            }
        } catch (Exception e) {
            // Em caso de erro ao processar a mensagem do Kafka
            System.err.println("Erro ao processar evento de pedido: " + e.getMessage());
        }
    }
}
