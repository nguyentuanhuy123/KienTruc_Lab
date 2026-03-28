package com.example.database_food_delivery.service;

import com.example.database_food_delivery.entity.Shipment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ShippingService {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void addShipment(Shipment shipment) {
        jdbcTemplate.update("INSERT INTO shipments (order_id, shipping_date, status) VALUES (?,?,?)",
                shipment.getOrderId(), shipment.getShippingDate(), shipment.getStatus());
    }

    public List<Shipment> getShipmentsByOrder(Integer orderId) {
        String sql = "SELECT * FROM shipments WHERE order_id = ?";
        return jdbcTemplate.query(sql, new Object[]{orderId}, (rs, rowNum) ->
                new Shipment(rs.getInt("id"),
                        rs.getInt("order_id"),
                        rs.getTimestamp("shipping_date").toLocalDateTime(),
                        rs.getString("status")));
    }
}
