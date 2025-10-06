package com.example.currencyrates.exception;

import com.example.currencyrates.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ServerWebExchange;

import java.time.OffsetDateTime;

@RestControllerAdvice
public class CurrencyRateControllerAdvice {

    private static String path(ServerWebExchange exchange) {
        return exchange != null && exchange.getRequest() != null
                ? exchange.getRequest().getPath().value()
                : "N/A";
    }

    @ExceptionHandler(UnknownCurrencyException.class)
    public ResponseEntity<ErrorResponse> handleUnknownCurrency(
            UnknownCurrencyException ex,
            ServerWebExchange exchange) {

        var body = new ErrorResponse(
                ex.getMessage(),
                path(exchange),
                OffsetDateTime.now(),
                ex.getValidCurrencies()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler({
            WebExchangeBindException.class,
            MethodArgumentNotValidException.class,
            BadRequestException.class
    })
    public ResponseEntity<ErrorResponse> handleBadRequest(Exception ex, ServerWebExchange exchange) {

        String message;
        if (ex instanceof WebExchangeBindException webEx && !webEx.getAllErrors().isEmpty()) {
            message = webEx.getAllErrors().get(0).getDefaultMessage();
        } else if (ex instanceof MethodArgumentNotValidException argEx && !argEx.getAllErrors().isEmpty()) {
            message = argEx.getAllErrors().get(0).getDefaultMessage();
        } else {
            message = ex.getMessage() != null ? ex.getMessage() : "Bad request";
        }

        var body = new ErrorResponse(message, path(exchange), OffsetDateTime.now(), null);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(ExternalServiceException.class)
    public ResponseEntity<ErrorResponse> handleExternalServiceError(
            ExternalServiceException ex,
            ServerWebExchange exchange) {

        var body = new ErrorResponse(
                ex.getMessage(),
                path(exchange),
                OffsetDateTime.now(),
                null
        );
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericError(
            Exception ex,
            ServerWebExchange exchange) {

        var body = new ErrorResponse(
                ex.getMessage() != null ? ex.getMessage() : "Internal server error",
                path(exchange),
                OffsetDateTime.now(),
                null
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }
}
