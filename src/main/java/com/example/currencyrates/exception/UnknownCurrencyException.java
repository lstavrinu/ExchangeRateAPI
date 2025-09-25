package com.example.currencyrates.exception;

import java.util.Set;

public class UnknownCurrencyException extends RuntimeException {

    private final String currency;
    private final Set<String> validCurrencies;

    public UnknownCurrencyException(String currency,  Set<String> validCurrencies)  {

        super("Unknown or unsuported currency: " + currency);
        this.currency = currency;
        this.validCurrencies = validCurrencies;
    }

    public String getCurrency() {
        return currency;
    }

    public Set<String> getValidCurrencies() {
        return validCurrencies;
    }
}
