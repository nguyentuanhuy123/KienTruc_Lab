package org.example.tax.decorator;


import org.example.tax.strategy.TaxStrategy;

public class SpecialTaxDecorator extends TaxDecorator {
    public SpecialTaxDecorator(TaxStrategy tax) {
        super(tax);
    }

    public double calculate(double price) {
        return tax.calculate(price) + price * 0.05;
    }
}
