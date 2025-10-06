package com.example.currencyrates.exception;

import lombok.Getter;

import java.util.Set;

@Getter
public class UnknownCurrencyException extends RuntimeException {

    private final String currency;
    private final Set<String> validCurrencies;

    public UnknownCurrencyException(String currency,  Set<String> validCurrencies)  {

        super("Unknown or unsuported currency: " + currency);
        this.currency = currency;
        this.validCurrencies = validCurrencies;
    }

}
