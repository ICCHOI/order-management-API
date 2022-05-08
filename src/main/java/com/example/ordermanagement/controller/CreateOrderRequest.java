package com.example.ordermanagement.controller;

import com.example.ordermanagement.model.OrderItem;

import java.util.List;

public record CreateOrderRequest(String email, String address, String postcode, List<OrderItem>orderItems) {
}
