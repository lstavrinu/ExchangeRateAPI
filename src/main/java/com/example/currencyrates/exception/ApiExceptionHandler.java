package com.example.currencyrates.exception;


import com.example.currencyrates.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ServerWebExchange;

import java.time.OffsetDateTime;

@RestControllerAdvice
public class ApiExceptionHandler {

    private static String path(ServerWebExchange exchange){

        return exchange != null && exchange.getRequest() != null
                ? exchange.getRequest().getPath().value()
                : "N/A";
    }

    @ExceptionHandler(UnknownCurrencyException.class)
    public ResponseEntity<ErrorResponse> handleUnknown(UnknownCurrencyException ex, ServerWebExchange exchange){

        ErrorResponse body = new ErrorResponse(
                ex.getMessage(),
                path(exchange),
                OffsetDateTime.now(),
                ex.getValidCurrencies()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(WebExchangeBindException.class)
    public ResponseEntity<ErrorResponse> handleWebExchangeBind(WebExchangeBindException ex, ServerWebExchange exchange){


        String msg = ex.getAllErrors().stream()
                .findFirst()
                .map(err -> (err instanceof FieldError fe)
                        ? fe.getField() + " " + (fe.getDefaultMessage() != null ? fe.getDefaultMessage() : "is invalid")
                        : (err.getDefaultMessage() != null ? err.getDefaultMessage() : "Validation Error"))
                .orElse("Validation error");

        return ResponseEntity.badRequest().body(ErrorResponse.of(msg, path(exchange)));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgNotValid(MethodArgumentNotValidException ex, ServerWebExchange exchange){

        String msg = ex.getBindingResult().getAllErrors().stream()
                .findFirst()
                .map(err -> err.getDefaultMessage() != null ? err.getDefaultMessage() : "Validation error")
                .orElse("Validation error");

        return ResponseEntity.badRequest().body(ErrorResponse.of(msg, path(exchange)));
    }


    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResponse> handleBadRequest(BadRequestException ex, ServerWebExchange exchange){

        return ResponseEntity.badRequest().body(ErrorResponse.of(ex.getMessage(), path(exchange)));
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception ex, ServerWebExchange exchange){

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorResponse.of(ex.getMessage(), path(exchange)));
    }

}
