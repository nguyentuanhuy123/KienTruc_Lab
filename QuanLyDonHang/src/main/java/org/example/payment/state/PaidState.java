package org.example.payment.state;

public class PaidState implements PaymentState {
    public void handle() {
        System.out.println("[PAYMENT] Thanh toán thành công");
    }
}
