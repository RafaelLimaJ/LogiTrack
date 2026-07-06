# Delivery Service

Serviço de gerenciamento de entregas de pedidos do LogiTrack.

## Funcionalidades
- Consome eventos do Kafka (`orders-topic`) para gerar entregas automáticas quando um pedido é criado.
- Permite atualizar o status da entrega (`EM_TRANSITO`, `ENTREGUE`).
- Publica eventos no Kafka (`deliveries-topic`) avisando as mudanças no status da entrega.

## Tecnologias
- Java 17
- Spring Boot
- Spring Security (JWT)
- Spring Kafka
- MySQL
