package org.example.tax.state;

public class NormalProductState implements ProductState {
    public double applyTax(double price) {
        return price * 0.1;
    }
}
