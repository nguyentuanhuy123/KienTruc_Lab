package org.example.order.state;

import org.example.order.OrderContext;

public interface OrderState {
    void handle(OrderContext context);
}
