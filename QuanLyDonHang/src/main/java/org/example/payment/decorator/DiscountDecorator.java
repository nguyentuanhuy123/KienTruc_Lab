package org.example.payment.decorator;

import org.example.payment.strategy.PaymentStrategy;

public class DiscountDecorator extends PaymentDecorator {
    public DiscountDecorator(PaymentStrategy payment) {
        super(payment);
    }

    public double pay(double amount) {
        return payment.pay(amount - 20);
    }
}
