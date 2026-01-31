package org.example.order.decorator;


public class LoggingOrderDecorator extends OrderDecorator {
    public LoggingOrderDecorator(OrderService service) {
        super(service);
    }

    public void execute() {
        System.out.println("[ORDER] Ghi log đơn hàng");
        service.execute();
    }
}