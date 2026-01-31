package org.example.order.decorator;

public class BasicOrderService implements OrderService {
    public void execute() {
        System.out.println("[ORDER] Thực hiện xử lý đơn hàng");
    }
}
