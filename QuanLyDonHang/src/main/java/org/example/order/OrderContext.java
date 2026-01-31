package org.example.order;
import org.example.order.state.NewOrderState;
import org.example.order.state.OrderState;
import org.example.order.strategy.OrderStrategy;

public class OrderContext {
    private OrderState state;
    private OrderStrategy strategy;

    public OrderContext(OrderStrategy strategy) {
        this.state = new NewOrderState();
        this.strategy = strategy;
    }

    public void setState(OrderState state) {
        this.state = state;
    }

    public void process() {
        strategy.process();
        state.handle(this);
    }
}

