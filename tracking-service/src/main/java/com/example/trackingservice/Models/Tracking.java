package com.example.trackingservice.Models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

// Documento do MongoDB que guarda o estado atual do rastreamento
@Document(collection = "trackings")
public class Tracking {
    @Id
    private String id;
    
    private Long deliveryId;
    private Long orderId;
    private String status;
    private String customerName;
    private String destinationAddress;
    private LocalDateTime lastUpdated;

    // Getters e Setters pra acessar os dados de rastreamento
    
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getDeliveryId() {
        return deliveryId;
    }

    public void setDeliveryId(Long deliveryId) {
        this.deliveryId = deliveryId;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getDestinationAddress() {
        return destinationAddress;
    }

    public void setDestinationAddress(String destinationAddress) {
        this.destinationAddress = destinationAddress;
    }

    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(LocalDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
}
