package com.example.currencyrates.service;

import com.example.currencyrates.client.ExchangeRatesClient;
import com.example.currencyrates.domain.CurrencyRate;
import com.example.currencyrates.exception.UnknownCurrencyException;
import com.example.currencyrates.repository.CurrencyRateRepository;
import com.example.currencyrates.util.CurrencyUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Locale;

@Service
public class CurrencyService {

    private final CurrencyRateRepository currencyRateRepository;
    private final ExchangeRatesClient exchangeRatesClient;

    public CurrencyService(CurrencyRateRepository currencyRateRepository, ExchangeRatesClient exchangeRatesClient) {
        this.currencyRateRepository = currencyRateRepository;
        this.exchangeRatesClient = exchangeRatesClient;
    }

    @Transactional
    public int fetchAndStoreLatest() {
        var latest = exchangeRatesClient.fetchLatest();
        var now = OffsetDateTime.now();

        var batch = latest.rates().entrySet().stream()
                .filter(e -> e.getValue() != null)
                .map(e -> new CurrencyRate(
                        latest.base().toUpperCase(Locale.ROOT),
                        e.getKey().toUpperCase(Locale.ROOT),
                        e.getValue(),
                        now))
                .toList();

        currencyRateRepository.saveAll(batch);
        return batch.size();
    }

    @Transactional(readOnly = true)
    public CurrencyRate getLatestFor(String currency) {
        if (!CurrencyUtils.isValidCurrency(currency)) {
            throw new UnknownCurrencyException(currency, CurrencyUtils.validIsoCurrencies());
        }
        return currencyRateRepository.findTopByQuoteCurrencyOrderByFetchedAtDesc(currency)
                .orElseThrow(() -> new UnknownCurrencyException(currency, CurrencyUtils.validIsoCurrencies()));
    }

    @Transactional(readOnly = true)
    public List<CurrencyRate> getHistory(String currency, OffsetDateTime from, OffsetDateTime to) {
        if (!CurrencyUtils.isValidCurrency(currency)) {
            throw new UnknownCurrencyException(currency, CurrencyUtils.validIsoCurrencies());
        }
        return currencyRateRepository.findByQuoteCurrencyAndFetchedAtBetweenOrderByFetchedAtDesc(currency, from, to);
    }
}
