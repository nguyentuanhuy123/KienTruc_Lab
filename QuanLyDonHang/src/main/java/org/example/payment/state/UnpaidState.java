package org.example.payment.state;

public class UnpaidState implements PaymentState {
    public void handle() {
        System.out.println("[PAYMENT] Chưa thanh toán");
    }
}
