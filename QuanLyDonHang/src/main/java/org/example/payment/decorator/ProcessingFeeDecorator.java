package org.example.payment.decorator;

import org.example.payment.strategy.PaymentStrategy;

public class ProcessingFeeDecorator extends PaymentDecorator {
    public ProcessingFeeDecorator(PaymentStrategy payment) {
        super(payment);
    }

    public double pay(double amount) {
        return payment.pay(amount + 10);
    }
}