# Order Service

Serviço de gerenciamento de pedidos (ordens de serviço).

## Funcionalidades
- Cadastro de pedidos (`POST /orders`).
- Atualização de status de pedidos (`PUT /orders/{id}/status`).
- Publicação de eventos de criação e atualização de pedidos no Kafka (`orders-topic`).

## Tecnologias
- Java 17
- Spring Boot
- Spring Security (JWT)
- Spring Kafka
- MySQL
