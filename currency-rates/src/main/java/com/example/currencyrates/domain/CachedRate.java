package com.example.currencyrates.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Getter
@AllArgsConstructor
public class CachedRate {

    private final BigDecimal rate;
    private final OffsetDateTime timestamp;

    public boolean isExpired() {
        return OffsetDateTime.now().minusSeconds(60).isAfter(timestamp);
    }
}
