package org.example.order.decorator;


public abstract class OrderDecorator implements OrderService {
    protected OrderService service;

    public OrderDecorator(OrderService service) {
        this.service = service;
    }
}
