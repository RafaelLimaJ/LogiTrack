package com.example.orderservice.Repositories;

import com.example.orderservice.Models.Order;
import org.springframework.data.jpa.repository.JpaRepository;

// Interface pra salvar e buscar pedidos no MySQL
public interface OrderRepository extends JpaRepository<Order, Long> {
}
