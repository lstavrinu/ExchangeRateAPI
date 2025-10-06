package com.example.currencyrates.controller;

import com.example.currencyrates.dto.CurrencyRateResponse;
import com.example.currencyrates.dto.RatesResponse;
import com.example.currencyrates.dto.ValidCurrenciesResponse;
import com.example.currencyrates.service.CurrencyService;
import com.example.currencyrates.util.CurrencyUtils;
import jakarta.validation.constraints.Pattern;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;

@RestController
@RequestMapping(path = "/api/v1/currencies", produces = MediaType.APPLICATION_JSON_VALUE)
@Validated
public class CurrencyController {

    private final CurrencyService currencyService;

    public CurrencyController(CurrencyService currencyService) {
        this.currencyService = currencyService;
    }

    // GET /api/v1/currencies/valid
    @GetMapping("/valid")
    public ValidCurrenciesResponse getValidCurrencies() {
        return new ValidCurrenciesResponse(CurrencyUtils.validIsoCurrencies());
    }


    // GET /api/v1/currencies/EUR
    @GetMapping("/{code}")
    public CurrencyRateResponse getLatestRate(
            @PathVariable("code")
            @Pattern(regexp = "^[A-Za-z]{3}$", message = "Currency must be a 3-letter ISO code")
            String code) {

        return CurrencyRateResponse.from(currencyService.getLatestFor(code.toUpperCase()));
    }


    // GET /api/v1/currencies/EUR/history?from=2025-10-01T00:00:00Z&to=2025-10-05T00:00:00Z
    @GetMapping("/{code}/history")
    public RatesResponse getCurrencyHistory(
            @PathVariable("code")
            @Pattern(regexp = "^[A-Za-z]{3}$", message = "Currency must be a 3-letter ISO code")
            String code,
            @RequestParam("from")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime from,
            @RequestParam("to")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime to) {

        var history = currencyService.getHistory(code.toUpperCase(), from, to)
                .stream()
                .map(CurrencyRateResponse::from)
                .toList();

        return new RatesResponse(history);
    }
}
