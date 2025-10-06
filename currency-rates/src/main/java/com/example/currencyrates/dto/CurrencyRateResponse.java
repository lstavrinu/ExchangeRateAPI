package com.example.currencyrates.dto;

import com.example.currencyrates.domain.CurrencyRate;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record CurrencyRateResponse (String base, String currency, BigDecimal rate, OffsetDateTime valid) {

    public static CurrencyRateResponse from(CurrencyRate cr) {
        return new CurrencyRateResponse(cr.getBaseCurrency(), cr.getQuoteCurrency(), cr.getRate(), cr.getFetchedAt());
    }

}
