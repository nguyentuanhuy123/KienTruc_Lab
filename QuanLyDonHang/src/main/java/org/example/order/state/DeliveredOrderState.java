package org.example.order.state;

import org.example.order.OrderContext;
public class DeliveredOrderState implements OrderState {
    public void handle(OrderContext context) {
        System.out.println("[ORDER] Đơn hàng đã giao");
    }
}
