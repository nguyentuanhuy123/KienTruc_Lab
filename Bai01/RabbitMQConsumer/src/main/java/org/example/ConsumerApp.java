package org.example;
import com.rabbitmq.client.*;
import java.nio.charset.StandardCharsets;

public class ConsumerApp {
    private final static String QUEUE_NAME = "order_task";

    public static void main(String[] argv) throws Exception {

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        factory.setUsername("guest");
        factory.setPassword("guest");

        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.queueDeclare(QUEUE_NAME, false, false, false, null);

        System.out.println(" [*] Java Consumer đang đợi đơn hàng... Nhấn Ctrl+C để dừng.");

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), StandardCharsets.UTF_8);

            System.out.println("------------------------------------------");
            System.out.println(" [v] Java đã nhận được dữ liệu từ Node.js!");
            System.out.println(" [v] Nội dung: " + message);

            try { Thread.sleep(1000); } catch (InterruptedException _ignored) {}
        };

        channel.basicConsume(QUEUE_NAME, true, deliverCallback, consumerTag -> { });
    }
}