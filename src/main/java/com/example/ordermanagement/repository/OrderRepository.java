package com.example.ordermanagement.repository;

import com.example.ordermanagement.model.Order;

import java.util.Optional;
import java.util.UUID;

public interface OrderRepository {

    Order insert(Order order);

    Optional<Order> findById(UUID orderId);

    void deleteById(UUID orderId);
}
