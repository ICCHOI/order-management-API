package com.example.ordermanagement.repository;

import com.example.ordermanagement.model.Category;
import com.example.ordermanagement.model.Order;
import com.example.ordermanagement.model.OrderItem;
import com.example.ordermanagement.model.OrderStatus;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;

import static com.example.ordermanagement.util.JdbcUtils.toLocalDateTime;
import static com.example.ordermanagement.util.JdbcUtils.toUUID;

@Repository
public class OrderJdbcRepository implements OrderRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public OrderJdbcRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    @Transactional
    public Order insert(Order order) {
        jdbcTemplate.update("INSERT INTO orders(order_id, email, address, postcode, order_status, order_at, updated_at) " +
                        "VALUES(UUID_TO_BIN(:orderId), :email, :address, :postcode, :orderStatus, :orderAt, :updatedAt)",
                toOrderParamMap(order));
        order.getOrderItems()
                .forEach(item -> jdbcTemplate.update("INSERT INTO order_items(order_id, product_id, category, price, quantity, order_at, updated_at) " +
                        "VALUES(UUID_TO_BIN(:orderId), UUID_TO_BIN(:productId), :category, :price, :quantity, :orderAt, :updatedAt)",
                toOrderItemParameterMap(order.getOrderId(), order.getOrderAt(), order.getUpdatedAt(), item)));
        return order;
    }

    @Override
    public Optional<Order> findById(UUID orderId) {
        try {
            return Optional.ofNullable(
                    jdbcTemplate.queryForObject(
                            "SELECT * FROM orders WHERE order_id = UUID_TO_BIN(:orderId)",
                            Collections.singletonMap("orderId", orderId.toString()), orderRowMapper));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public void deleteById(UUID orderId) {
        var parameterMap = Collections.singletonMap("orderId", orderId.toString());
        jdbcTemplate.update("DELETE FROM order_items WHERE order_id = UUID_TO_BIN(:orderId)", parameterMap);
        jdbcTemplate.update("DELETE FROM orders WHERE order_id = UUID_TO_BIN(:orderId)", parameterMap);
    }

    private Map<String, Object> toOrderParamMap(Order order) {
        var parameterMap = new HashMap<String, Object>();
        parameterMap.put("orderId", order.getOrderId().toString().getBytes(StandardCharsets.UTF_8));
        parameterMap.put("email", order.getEmail().getAddress());
        parameterMap.put("address", order.getAddress());
        parameterMap.put("postcode", order.getPostcode());
        parameterMap.put("orderStatus", order.getOrderStatus().toString());
        parameterMap.put("orderAt", order.getOrderAt());
        parameterMap.put("updatedAt", order.getUpdatedAt());
        return parameterMap;
    }

    private Map<String, Object> toOrderItemParameterMap(UUID orderId, LocalDateTime orderAt, LocalDateTime updatedAt, OrderItem item) {
        var parameterMap = new HashMap<String, Object>();
        parameterMap.put("orderId", orderId.toString().getBytes());
        parameterMap.put("productId", item.productId().toString().getBytes());
        parameterMap.put("category", item.category().toString());
        parameterMap.put("price", item.price());
        parameterMap.put("quantity", item.quantity());
        parameterMap.put("orderAt", orderAt);
        parameterMap.put("updatedAt", updatedAt);
        return parameterMap;
    }

    private final RowMapper<Order> orderRowMapper = (resultSet, number) -> {
        var orderId = toUUID(resultSet.getBytes("order_id"));
        var email = resultSet.getString("email");
        var address = resultSet.getString("address");
        var postcode = resultSet.getString("postcode");
        var orderItems = findOrderItem(orderId);
        var orderStatus = OrderStatus.valueOf(resultSet.getString("order_status"));
        var orderAt = toLocalDateTime(resultSet.getTimestamp("order_at"));
        var updatedAt = toLocalDateTime(resultSet.getTimestamp("updated_at"));

        return new Order(orderId, email, address, postcode, orderItems, orderStatus, orderAt, updatedAt);
    };

    private List<OrderItem> findOrderItem(UUID orderId) {
        return jdbcTemplate.query("SELECT * FROM order_items WHERE order_id = UUID_TO_BIN(:orderId)", Collections.singletonMap("orderId", orderId.toString()), orderItemRowMapper);
    }

    private final RowMapper<OrderItem> orderItemRowMapper = (resultSet, number) -> {
        var productId = toUUID(resultSet.getBytes("product_id"));
        var category = Category.valueOf(resultSet.getString("category"));
        var price = resultSet.getLong("price");
        var quantity = resultSet.getInt("quantity");

        return new OrderItem(productId, category, price, quantity);
    };
}
