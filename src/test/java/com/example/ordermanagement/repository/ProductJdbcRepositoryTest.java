package com.example.ordermanagement.repository;

import com.example.ordermanagement.config.DbConfig;
import com.example.ordermanagement.model.Category;
import com.example.ordermanagement.model.Product;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.UUID;

import static com.example.ordermanagement.config.DbConfig.dbSetup;
import static org.assertj.core.api.Assertions.assertThat;

@SpringJUnitConfig
@ContextConfiguration(classes = {DbConfig.class})
class ProductJdbcRepositoryTest {
    @Autowired
    ProductRepository repository;

    @BeforeAll
    static void setup() {
        dbSetup();
    }

    @AfterEach
    void cleanup() {
        repository.deleteAll();
    }

    @Test
    @DisplayName("상품을 추가할 수 있다.")
    void testInsert() {
        Product newProduct = new Product(UUID.randomUUID(), "new-product", Category.COFFEE_BEAN_PACKAGE, 1000L);
        repository.insert(newProduct);

        var all = repository.findAll();

        assertThat(all).isNotEmpty();
    }

    @Test
    @DisplayName("상품을 이름으로 조회할 수 있다.")
    void testFindByName() {
        Product newProduct = new Product(UUID.randomUUID(), "new-product", Category.COFFEE_BEAN_PACKAGE, 1000L);
        repository.insert(newProduct);

        var product = repository.findByName(newProduct.getProductName());

        assertThat(product).isPresent();
    }

    @Test
    @DisplayName("상품을 아이디로 조회할 수 있다.")
    void testFindById() {
        Product newProduct = new Product(UUID.randomUUID(), "new-product", Category.COFFEE_BEAN_PACKAGE, 1000L);
        repository.insert(newProduct);

        var product = repository.findById(newProduct.getProductId());

        assertThat(product).isPresent();
    }

    @Test
    @DisplayName("상품들을 카테고리로 조회할 수 있다.")
    void testFindByCategory() {
        Product newProduct = new Product(UUID.randomUUID(), "new-product", Category.COFFEE_BEAN_PACKAGE, 1000L);
        repository.insert(newProduct);

        var product = repository.findByCategory(newProduct.getCategory());

        assertThat(product).isNotEmpty();
    }

    @Test
    @DisplayName("상품을 수정할 수 있다.")
    void testUpdate() {
        Product newProduct = new Product(UUID.randomUUID(), "new-product", Category.COFFEE_BEAN_PACKAGE, 1000L);
        repository.insert(newProduct);

        newProduct.setProductName("updated-product");
        repository.update(newProduct);

        var product = repository.findById(newProduct.getProductId());
        assertThat(product).isNotEmpty().get().isEqualTo(newProduct);
    }

    @Test
    @DisplayName("상품을 전체 삭제할 수 있다.")
    void testDeleteAll() {
        repository.deleteAll();

        var empty = repository.findAll();

        assertThat(empty).isEmpty();
    }
}