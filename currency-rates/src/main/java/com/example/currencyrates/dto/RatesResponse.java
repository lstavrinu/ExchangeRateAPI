package com.example.currencyrates.dto;

import java.util.List;

public record RatesResponse(List<CurrencyRateResponse> rates) {

}
