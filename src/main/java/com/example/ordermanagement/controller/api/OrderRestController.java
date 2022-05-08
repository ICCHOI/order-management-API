package com.example.ordermanagement.controller.api;

import com.example.ordermanagement.controller.CreateOrderRequest;
import com.example.ordermanagement.model.Email;
import com.example.ordermanagement.model.Order;
import com.example.ordermanagement.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
public class OrderRestController {

    private final OrderService orderService;

    public OrderRestController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/api/v1/orders")
    public Order createOrder(@RequestBody CreateOrderRequest orderRequest) {
        return orderService.createOrder(
                new Email(orderRequest.email()),
                orderRequest.address(),
                orderRequest.postcode(),
                orderRequest.orderItems()
        );
    }

    @GetMapping("/api/v1/orders/{orderId}")
    public ResponseEntity<Order> findOrder(@PathVariable("orderId") UUID orderId) {
        var optionalOrder = orderService.getOrder(orderId);
        return optionalOrder.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/api/v1/orders/{orderId}")
    public void cancelOrder(@PathVariable("orderId") UUID orderId) {
        orderService.cancelOrder(orderId);
    }
}
