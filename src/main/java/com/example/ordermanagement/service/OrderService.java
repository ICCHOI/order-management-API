package com.example.ordermanagement.service;

import com.example.ordermanagement.model.Email;
import com.example.ordermanagement.model.Order;
import com.example.ordermanagement.model.OrderItem;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderService {
    Order createOrder(Email email, String address, String postcode, List<OrderItem> orderItems);

    Optional<Order> getOrder(UUID orderId);

    void cancelOrder(UUID orderId);
}
