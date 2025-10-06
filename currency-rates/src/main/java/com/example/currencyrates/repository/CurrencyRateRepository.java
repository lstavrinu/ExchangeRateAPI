package com.example.currencyrates.repository;

import com.example.currencyrates.domain.CurrencyRate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

public interface CurrencyRateRepository extends JpaRepository<CurrencyRate, Long> {

    // Latest record for a currency
    Optional<CurrencyRate> findTopByQuoteCurrencyOrderByFetchedAtDesc(String ccy);

    // Historical range
    List<CurrencyRate> findByQuoteCurrencyAndFetchedAtBetweenOrderByFetchedAtDesc(String ccy, OffsetDateTime from, OffsetDateTime to);
}
