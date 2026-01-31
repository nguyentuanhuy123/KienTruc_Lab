package org.example;

import org.example.order.OrderContext;
import org.example.order.decorator.BasicOrderService;
import org.example.order.decorator.LoggingOrderDecorator;
import org.example.order.decorator.OrderService;
import org.example.order.strategy.FastOrderStrategy;
import org.example.payment.decorator.DiscountDecorator;
import org.example.payment.decorator.ProcessingFeeDecorator;
import org.example.payment.strategy.CreditCardPayment;
import org.example.payment.strategy.PaymentStrategy;
import org.example.tax.decorator.SpecialTaxDecorator;
import org.example.tax.strategy.TaxStrategy;
import org.example.tax.strategy.VATTax;

// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.
public class Main {
    public static void main(String[] args) {

        OrderContext order = new OrderContext(new FastOrderStrategy());
        OrderService orderService =
                new LoggingOrderDecorator(new BasicOrderService());

        orderService.execute();
        order.process();
        order.process();

        TaxStrategy tax = new VATTax();
        tax = new SpecialTaxDecorator(tax);

        double price = 1000;
        System.out.println("[TAX] Tổng thuế: " + tax.calculate(price));

        PaymentStrategy payment =
                new DiscountDecorator(
                        new ProcessingFeeDecorator(
                                new CreditCardPayment()
                        )
                );

        double finalAmount = payment.pay(price);
        System.out.println("[PAYMENT] Số tiền phải trả: " + finalAmount);
    }
}