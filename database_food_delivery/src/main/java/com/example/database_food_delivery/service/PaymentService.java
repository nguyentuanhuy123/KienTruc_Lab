package com.example.database_food_delivery.service;

import com.example.database_food_delivery.entity.Payment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PaymentService {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void addPayment(Payment payment) {
        jdbcTemplate.update("INSERT INTO payments (order_id, payment_date, amount, method) VALUES (?,?,?,?)",
                payment.getOrderId(), payment.getPaymentDate(), payment.getAmount(), payment.getMethod());
    }

    public List<Payment> getPaymentsByOrder(Integer orderId) {
        String sql = "SELECT * FROM payments WHERE order_id = ?";
        return jdbcTemplate.query(sql, new Object[]{orderId}, (rs, rowNum) ->
                new Payment(rs.getInt("id"),
                        rs.getInt("order_id"),
                        rs.getTimestamp("payment_date").toLocalDateTime(),
                        rs.getBigDecimal("amount"),
                        rs.getString("method")));
    }
}

