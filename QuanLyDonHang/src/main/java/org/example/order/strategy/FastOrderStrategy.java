package org.example.order.strategy;

public class FastOrderStrategy implements OrderStrategy {
    public void process() {
        System.out.println("[ORDER] Xử lý đơn hàng nhanh");
    }
}