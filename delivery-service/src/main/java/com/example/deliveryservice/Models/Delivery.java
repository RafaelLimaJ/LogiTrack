package com.example.deliveryservice.Models;

import jakarta.persistence.*;

// Entidade que representa uma Entrega no MySQL
@Entity
@Table(name = "Deliveries")
public class Delivery {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
    private Long orderId;
    private String status; // CRIADO, EM_TRANSITO, ENTREGUE, etc.
    private String customerName;
    private String destinationAddress;

    // Getters e Setters pra acessar os dados da entrega
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
}
