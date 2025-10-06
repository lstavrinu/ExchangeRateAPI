package com.example.currencyrates.util;

import java.util.Currency;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public final class CurrencyUtils {

    private static final Set<String> VALID = new HashSet<>();

    static {
        for (Locale locale : Locale.getAvailableLocales()) {
            try {
                VALID.add(Currency.getInstance(locale).getCurrencyCode());
            } catch (Exception ignored) {}
        }

        VALID.addAll(Set.of(
                "USD", "EUR", "GBP", "JPY", "CHF", "AUD", "CAD",
                "CNY", "NZD", "SEK", "NOK", "DKK", "INR", "BRL",
                "MXN", "ZAR", "SGD", "KRW"
        ));
    }

    private CurrencyUtils() {}

    public static boolean isValidCurrency(String code) {
        return code != null && VALID.contains(code.toUpperCase());
    }

    public static Set<String> validIsoCurrencies() {
        return Set.copyOf(VALID);
    }
}
