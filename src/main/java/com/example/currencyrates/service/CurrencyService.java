package com.example.currencyrates.service;

import com.example.currencyrates.domain.CachedRate;
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

    private final CurrencyRateRepository repo;
    private final ExchangeRatesClient client;
    private final CurrencyCache cache;   // ✅ custom in-memory cache

    public CurrencyService(CurrencyRateRepository repo,
                           ExchangeRatesClient client,
                           CurrencyCache cache) {
        this.repo = repo;
        this.client = client;
        this.cache = cache;
    }

    @Transactional
    public int fetchAndStoreLatest() {
        var latest = client.fetchLatest();
        String base = latest.base().toUpperCase(Locale.ROOT);
        OffsetDateTime now = OffsetDateTime.now();

        var batch = latest.rates().entrySet().stream()
                .filter(e -> e.getValue() != null)
                .map(e -> new CurrencyRate(
                        base,
                        e.getKey().toUpperCase(Locale.ROOT),
                        e.getValue(),
                        now))
                .toList();

        repo.saveAll(batch);
        return batch.size();
    }

    @Transactional(readOnly = true)
    public CurrencyRate getLatestFor(String currency) {
        String c = currency.toUpperCase(Locale.ROOT);

        if (!CurrencyUtils.isValidCurrency(c)) {
            throw new UnknownCurrencyException(c, CurrencyUtils.validIsoCurrencies());
        }

        var cached = cache.get(c);
        if (cached.isPresent()) {
            CachedRate cr = cached.get();
            return new CurrencyRate("USD", c, cr.getRate(), cr.getTimestamp());
        }

        CurrencyRate latest = repo.findTopByQuoteCurrencyOrderByFetchedAtDesc(c)
                .orElseThrow(() -> new UnknownCurrencyException(c, CurrencyUtils.validIsoCurrencies()));

        cache.put(c, new CachedRate(latest.getRate(), latest.getFetchedAt()));

        return latest;
    }

    @Transactional(readOnly = true)
    public List<CurrencyRate> getHistory(String currency, OffsetDateTime from, OffsetDateTime to) {
        String c = currency.toUpperCase(Locale.ROOT);

        if (!CurrencyUtils.isValidCurrency(c)) {
            throw new UnknownCurrencyException(c, CurrencyUtils.validIsoCurrencies());
        }

        return repo.findByQuoteCurrencyAndFetchedAtBetweenOrderByFetchedAtDesc(c, from, to);
    }
}
