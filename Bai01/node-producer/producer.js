const amqp = require('amqplib');

async function pushOrder() {
    console.log("=== Bắt đầu kết nối RabbitMQ ===");
    try {
        const connection = await amqp.connect('amqp://localhost');
        const channel = await connection.createChannel();

        const queueName = 'order_task';

        await channel.assertQueue(queueName, { durable: false });

        const orderData = {
            orderId: Math.floor(Math.random() * 1000),
            item: "Laptop Gaming",
            time: new Date().toISOString()
        };
        const message = JSON.stringify(orderData);

        channel.sendToQueue(queueName, Buffer.from(message));
        
        console.log(`[Node.js] 🚀 Đã gửi đơn hàng thành công: ${message}`);

        setTimeout(() => {
            connection.close();
            console.log("=== Đã đóng kết nối thành công ===");
            process.exit(0);
        }, 500);

    } catch (error) {
        console.error("❌ Lỗi xảy ra:", error.message);
    }
}

pushOrder();