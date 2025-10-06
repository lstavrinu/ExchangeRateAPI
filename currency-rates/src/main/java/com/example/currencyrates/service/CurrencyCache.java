package com.example.currencyrates.service;

import com.example.currencyrates.domain.CachedRate;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Optional;

import org.springframework.stereotype.Component;

@Component
public class CurrencyCache {

    private final ConcurrentHashMap<String, CachedRate> cache = new ConcurrentHashMap<>();

    public Optional<CachedRate> get(String currency) {
        CachedRate cr = cache.get(currency);
        if (cr == null || cr.isExpired()) {
            cache.remove(currency);
            return Optional.empty();
        }
        return Optional.of(cr);
    }

    public void put(String currency, CachedRate rate) {
        cache.put(currency, rate);
    }

    public void clear() {
        cache.clear();
    }
}
