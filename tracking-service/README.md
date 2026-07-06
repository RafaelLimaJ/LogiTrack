# Tracking Service

Serviço de rastreamento de entregas integrado com MongoDB e AWS LocalStack.

## Funcionalidades
- Consome eventos do Kafka (`deliveries-topic`) para atualizar o estado do rastreamento.
- Salva o estado atualizado no **MongoDB** (NoSQL).
- Notifica eventos de mudança de rota na fila **AWS SQS** `tracking-notifications-queue` simulada pelo LocalStack.
- Armazena logs históricos detalhados na tabela **AWS DynamoDB** `TrackingLogs` simulada pelo LocalStack.
- Cria dinamicamente as filas e tabelas necessárias no LocalStack na inicialização.

## Tecnologias
- Java 17
- Spring Boot
- Spring Security (JWT)
- Spring Data MongoDB (NoSQL)
- Spring Kafka
- AWS SDK v2 (SQS, DynamoDB)
- LocalStack
