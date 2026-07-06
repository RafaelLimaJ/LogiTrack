package com.example.trackingservice.Kafka;

import com.example.trackingservice.Models.Tracking;
import com.example.trackingservice.Repositories.TrackingRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

// Classe que consome eventos do delivery-service e registra no MongoDB, SQS e DynamoDB
@Service
public class DeliveryConsumer {

    private final TrackingRepository trackingRepository;
    private final SqsClient sqsClient;
    private final DynamoDbClient dynamoDbClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private String queueUrl;

    public DeliveryConsumer(TrackingRepository trackingRepository, SqsClient sqsClient, DynamoDbClient dynamoDbClient) {
        this.trackingRepository = trackingRepository;
        this.sqsClient = sqsClient;
        this.dynamoDbClient = dynamoDbClient;
    }

    // Cria as filas e tabelas no LocalStack assim que o serviço sobe
    @PostConstruct
    public void setupLocalStackResources() {
        // Inicializa fila SQS
        try {
            CreateQueueResponse createQueueResponse = sqsClient.createQueue(
                    CreateQueueRequest.builder().queueName("tracking-notifications-queue").build()
            );
            this.queueUrl = createQueueResponse.queueUrl();
            System.out.println("Fila SQS criada com sucesso no LocalStack: " + queueUrl);
        } catch (Exception e) {
            // Se já existir ou der erro temporário, tenta pegar a URL
            try {
                this.queueUrl = sqsClient.getQueueUrl(
                        GetQueueUrlRequest.builder().queueName("tracking-notifications-queue").build()
                ).queueUrl();
            } catch (Exception ex) {
                System.err.println("Erro ao inicializar fila SQS no LocalStack: " + ex.getMessage());
            }
        }

        // Inicializa tabela DynamoDB
        try {
            dynamoDbClient.createTable(CreateTableRequest.builder()
                    .tableName("TrackingLogs")
                    .keySchema(KeySchemaElement.builder().attributeName("logId").keyType(KeyType.HASH).build())
                    .attributeDefinitions(AttributeDefinition.builder().attributeName("logId").attributeType(ScalarAttributeType.S).build())
                    .provisionedThroughput(ProvisionedThroughput.builder().readCapacityUnits(5L).writeCapacityUnits(5L).build())
                    .build());
            System.out.println("Tabela DynamoDB 'TrackingLogs' criada no LocalStack!");
        } catch (ResourceInUseException e) {
            // Tabela já existe, tudo certo!
            System.out.println("Tabela DynamoDB 'TrackingLogs' já existe.");
        } catch (Exception e) {
            System.err.println("Erro ao inicializar tabela DynamoDB no LocalStack: " + e.getMessage());
        }
    }

    // Escuta eventos de entregas atualizadas (deliveries-topic)
    @KafkaListener(topics = "deliveries-topic", groupId = "tracking-group")
    public void consumeDeliveryEvent(String eventMessage) {
        try {
            // Desserializa o JSON do evento
            JsonNode jsonNode = objectMapper.readTree(eventMessage);
            Long deliveryId = jsonNode.get("deliveryId").asLong();
            Long orderId = jsonNode.get("orderId").asLong();
            String status = jsonNode.get("status").asText();
            String customerName = jsonNode.get("customerName").asText();
            String destinationAddress = jsonNode.get("destinationAddress").asText();

            // 1. Salva ou atualiza no MongoDB
            Tracking tracking = trackingRepository.findByDeliveryId(deliveryId)
                    .orElse(new Tracking());
            
            tracking.setDeliveryId(deliveryId);
            tracking.setOrderId(orderId);
            tracking.setStatus(status);
            tracking.setCustomerName(customerName);
            tracking.setDestinationAddress(destinationAddress);
            tracking.setLastUpdated(LocalDateTime.now());
            
            trackingRepository.save(tracking);
            System.out.println("Tracking salvo no MongoDB para entrega: " + deliveryId);

            // 2. Envia notificação para a fila SQS do LocalStack
            if (queueUrl != null) {
                String sqsMsg = String.format("Aviso: A entrega %d do pedido %d mudou de status para %s",
                        deliveryId, orderId, status);
                sqsClient.sendMessage(SendMessageRequest.builder()
                        .queueUrl(queueUrl)
                        .messageBody(sqsMsg)
                        .build());
                System.out.println("Mensagem de rastreamento enviada ao SQS!");
            }

            // 3. Salva histórico detalhado de logs no DynamoDB do LocalStack
            Map<String, AttributeValue> item = new HashMap<>();
            item.put("logId", AttributeValue.builder().s(UUID.randomUUID().toString()).build());
            item.put("deliveryId", AttributeValue.builder().n(String.valueOf(deliveryId)).build());
            item.put("status", AttributeValue.builder().s(status).build());
            item.put("timestamp", AttributeValue.builder().s(LocalDateTime.now().toString()).build());
            
            dynamoDbClient.putItem(PutItemRequest.builder()
                    .tableName("TrackingLogs")
                    .item(item)
                    .build());
            System.out.println("Histórico de rastreamento salvo no DynamoDB!");

        } catch (Exception e) {
            System.err.println("Erro ao processar evento de rastreamento: " + e.getMessage());
        }
    }
}
