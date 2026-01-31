package org.example.tax.strategy;

public class VATTax implements TaxStrategy {
    public double calculate(double price) {
        return price * 0.1;
    }
}