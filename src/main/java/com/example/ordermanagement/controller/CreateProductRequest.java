package com.example.ordermanagement.controller;

import com.example.ordermanagement.model.Category;

public record CreateProductRequest(String productName, Category category, long price, String description) {
}
