package com.example.currencyrates.dto;

import java.math.BigDecimal;
import java.util.Map;

public record LiveResponse (
        boolean success,
        String source,
        long timestamp,
        Map<String, BigDecimal> quotes
){}
