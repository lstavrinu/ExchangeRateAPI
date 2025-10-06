package com.example.currencyrates.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(name= "currency_rate")
@AllArgsConstructor
@NoArgsConstructor
public class CurrencyRate {

    @Id
    @Getter
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Getter @Setter
    @Column(name = "base_currency",length = 3, nullable = false)
    private String baseCurrency;

    @Getter @Setter
    @Column(name = "quote_currency",length = 3, nullable = false)
    private String quoteCurrency;

    @Getter @Setter
    @Column(name = "rate", precision = 38, scale = 20, nullable = false)
    private BigDecimal rate;

    @Getter @Setter
    @Column(name = "fetched_at", nullable = false)
    private OffsetDateTime fetchedAt;

    public CurrencyRate(String baseCurrency, String quoteCurrency, BigDecimal rate, OffsetDateTime fetchedAt) {

        this.baseCurrency = baseCurrency;
        this.quoteCurrency = quoteCurrency;
        this.rate = rate;
        this.fetchedAt = fetchedAt;

    }

}
