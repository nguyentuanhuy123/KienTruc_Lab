package com.example.database_food_delivery.service;

import com.example.database_food_delivery.entity.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void createOrder(Order order) {
        String sql = "INSERT INTO orders (user_id, order_date, total) VALUES (?,?,?)";
        jdbcTemplate.update(sql, order.getUserId(), order.getOrderDate(), order.getTotal());
    }

    public List<Order> getOrdersByUser(Integer userId) {
        String sql = "SELECT * FROM orders WHERE user_id = ?";
        return jdbcTemplate.query(sql, new Object[]{userId}, (rs, rowNum) ->
                new Order(rs.getInt("id"), rs.getInt("user_id"), rs.getTimestamp("order_date").toLocalDateTime(), rs.getBigDecimal("total"))
        );
    }
}
