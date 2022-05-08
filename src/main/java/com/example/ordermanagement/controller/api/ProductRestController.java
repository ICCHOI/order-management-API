package com.example.ordermanagement.controller.api;

import com.example.ordermanagement.controller.CreateProductRequest;
import com.example.ordermanagement.model.Category;
import com.example.ordermanagement.model.Product;
import com.example.ordermanagement.service.ProductService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
public class ProductRestController {

    private final ProductService productService;

    public ProductRestController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping("/api/v1/products")
    public Product createOrder(@RequestBody CreateProductRequest productRequest) {
        return productService.createProduct(
                productRequest.productName(),
                productRequest.category(),
                productRequest.price(),
                productRequest.description()
        );
    }

    @GetMapping("/api/v1/products")
    public List<Product> productList(@RequestParam Optional<Category> category) {
        return category
                .map(productService::getProductsByCategory)
                .orElse(productService.getAllProducts());
    }

    @DeleteMapping("/api/v1/products")
    public void deleteAllProducts() {
        productService.deleteAllProduct();
    }
}
