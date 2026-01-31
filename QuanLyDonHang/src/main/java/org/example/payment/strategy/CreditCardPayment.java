package org.example.payment.strategy;

public class CreditCardPayment implements PaymentStrategy {
    public double pay(double amount) {
        System.out.println("[PAYMENT] Thanh toán bằng thẻ tín dụng");
        return amount;
    }
}
