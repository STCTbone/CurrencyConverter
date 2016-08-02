package com.example.matthewrieger.currencyconverter.model;

import java.io.Serializable;

public class ConversionRate implements Serializable {

    public String name;
    public String from;
    public String to;
    public Float rate;

    public Float convert(String amount) {
        try {
            Float floatAmount = Float.parseFloat(amount);
            return convert(floatAmount);
        } catch (NumberFormatException e) {
            return 0.0f;
        }
    }

    public Float convert(Float floatAmount) {
        Float convertedAmount = floatAmount * rate;
        return convertedAmount;
    }

    public Float inverse(Float floatAmount) {
        return 1 / convert(floatAmount);
    }

    @Override public String toString() {
        return "ConversionRate{"  +
                "name='" + name + '\'' +
                ", from='" + from + '\'' +
                ", to='" + to + '\'' +
                ", rate=" + rate +
                '}';
    }
}
