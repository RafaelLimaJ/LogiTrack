package com.example.trackingservice.Aws;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.sqs.SqsClient;

import java.net.URI;

// Configuração dos clientes AWS apontando para o LocalStack local
@Configuration
public class AwsConfig {

    @Value("${aws.localstack.endpoint}")
    private String localstackEndpoint;

    @Value("${aws.region}")
    private String region;

    // Cliente para enviar mensagens pro SQS do LocalStack
    @Bean
    public SqsClient sqsClient() {
        return SqsClient.builder()
                .endpointOverride(URI.create(localstackEndpoint))
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create("test", "test"))) // LocalStack não valida chaves reais
                .build();
    }

    // Cliente para salvar logs no DynamoDB do LocalStack
    @Bean
    public DynamoDbClient dynamoDbClient() {
        return DynamoDbClient.builder()
                .endpointOverride(URI.create(localstackEndpoint))
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create("test", "test")))
                .build();
    }
}
