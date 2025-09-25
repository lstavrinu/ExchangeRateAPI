package com.example.currencyrates.service;

import com.example.currencyrates.domain.CurrencyRate;
import com.example.currencyrates.exception.UnknownCurrencyException;
import com.example.currencyrates.repository.CurrencyRateRepository;
import com.example.currencyrates.util.CurrencyUtils;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
public class CurrencyService {

    private final CurrencyRateRepository repo;
    private final ExchangeRatesClient client;

    public CurrencyService(CurrencyRateRepository repo, ExchangeRatesClient client) {
        this.repo = repo;
        this.client = client;
    }

    @Transactional
    public int fetchAndStoreLatest(){
        ExchangeRatesClient.LatestResponse latest = client.fetchLatest();
        String base = latest.base().toUpperCase(Locale.ROOT);
        OffsetDateTime now = OffsetDateTime.now();

        int count = 0;
        for (Map.Entry<String, BigDecimal> e : latest.rates().entrySet()) {

            CurrencyRate cr = new CurrencyRate(base, e.getKey().toUpperCase(Locale.ROOT), e.getValue(), null);
            repo.save(cr);
            count++;
        }
        return count;
    }

    @Transactional(readOnly = true)
    public CurrencyRate getLatestFor(String currency){
        String c = currency.toUpperCase(Locale.ROOT);

        if(!CurrencyUtils.isValidCurrency(c)){
            throw new UnknownCurrencyException(c, CurrencyUtils.validIsoCurrencies());
        }
        return repo.findTopByQuoteCurrencyOrderByFetchedAtDesc(c)
                .orElseThrow(() -> new UnknownCurrencyException(c, CurrencyUtils.validIsoCurrencies()));
    }


    @Transactional(readOnly = true)
    public List<CurrencyRate> getHistory(String currency, OffsetDateTime from, OffsetDateTime to){
        String c = currency.toUpperCase(Locale.ROOT);
        if(!CurrencyUtils.isValidCurrency(c)){
            throw new UnknownCurrencyException(c, CurrencyUtils.validIsoCurrencies());
        }

        return repo.findByQuoteCurrencyAndFetchedAtBetweenOrderByFetchedAtDesc(c, from, to);
    }

}
