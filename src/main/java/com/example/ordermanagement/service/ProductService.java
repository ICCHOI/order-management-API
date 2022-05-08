package com.example.ordermanagement.service;

import com.example.ordermanagement.model.Category;
import com.example.ordermanagement.model.Product;

import java.util.List;

public interface ProductService {

    List<Product> getProductsByCategory(Category category);

    List<Product> getAllProducts();

    Product createProduct(String productName, Category category, long price, String description);

    void deleteAllProduct();
}
