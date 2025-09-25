package com.example.currencyrates.controller;


import com.example.currencyrates.domain.CurrencyRate;
import com.example.currencyrates.dto.CurrencyRateResponse;
import com.example.currencyrates.dto.ValidCurrenciesResponse;
import com.example.currencyrates.service.CurrencyService;
import com.example.currencyrates.util.CurrencyUtils;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.DateTimeException;
import java.time.OffsetDateTime;
import java.util.List;

@RestController
@RequestMapping(path = "/api/v1/currencies", produces = MediaType.APPLICATION_JSON_VALUE)
@Validated
public class CurrencyController {

    private final CurrencyService service;

    public CurrencyController(CurrencyService service) {
        this.service = service;
    }

    @GetMapping("/valid")
    public ValidCurrenciesResponse validCurrencies() {
        return new ValidCurrenciesResponse(CurrencyUtils.validIsoCurrencies());
    }

    // GET /api/v1/currencies/{code}
    @GetMapping("/{code}")
    public CurrencyRateResponse latest(
            @PathVariable("code")
            @Pattern(regexp = "^[A-Za-z]{3}$", message = "Currency must be 3 letters")
            String code) {

        CurrencyRate cr = service.getLatestFor(code.toUpperCase());
        return CurrencyRateResponse.from(cr);
    }

    // GET /api/v1/currencies/{code}/history?from=...&to=...
    @GetMapping("/{code}/history")
    public List<CurrencyRateResponse> history(
            @PathVariable("code") @Pattern(regexp = "^[A-Za-z]{3}$") String code,
            @RequestParam("from") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime from,
            @RequestParam("to")   @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime to) {

        if (from.isAfter(to)) {
            throw new IllegalArgumentException("'from' must be before 'to'");
        }

        return service.getHistory(code.toUpperCase(), from, to)
                .stream()
                .map(CurrencyRateResponse::from)
                .toList();
    }
}
