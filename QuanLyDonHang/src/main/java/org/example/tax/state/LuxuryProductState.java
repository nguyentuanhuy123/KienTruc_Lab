package org.example.tax.state;

public class LuxuryProductState implements ProductState {
    public double applyTax(double price) {
        return price * 0.3;
    }
}