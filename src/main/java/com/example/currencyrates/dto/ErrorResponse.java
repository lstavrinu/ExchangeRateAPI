package com.example.currencyrates.dto;

import java.time.OffsetDateTime;
import java.util.Set;

public record ErrorResponse (
        String message,
        String path,
        OffsetDateTime timestamp,
        Set<String> validCurrencies
) {

    public static ErrorResponse of(String message, String path){
        return new ErrorResponse(message, path, OffsetDateTime.now(), null);
    }
}
