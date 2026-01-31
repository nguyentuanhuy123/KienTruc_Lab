package org.example.order.state;

import org.example.order.OrderContext;

public class NewOrderState implements OrderState {
    public void handle(OrderContext context) {
        System.out.println("[ORDER] Kiểm tra thông tin đơn hàng");
        context.setState(new ProcessingOrderState());
    }
}
