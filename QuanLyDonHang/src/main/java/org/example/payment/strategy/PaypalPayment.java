package org.example.payment.strategy;

public class PaypalPayment implements PaymentStrategy {
    public double pay(double amount) {
        System.out.println("[PAYMENT] Thanh toán bằng PayPal");
        return amount;
    }
}
