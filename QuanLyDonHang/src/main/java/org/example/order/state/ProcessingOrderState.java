package org.example.order.state;

import org.example.order.OrderContext;
public class ProcessingOrderState implements OrderState {
    @Override
    public void handle(OrderContext context) {
        System.out.println("[ORDER] Đóng gói và vận chuyển");
        context.setState(new DeliveredOrderState());
    }
}
