package com.example.currencyrates;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CurrencyRatesApplication {

    public static void main(String[] args) {

        SpringApplication.run(CurrencyRatesApplication.class, args);
        
    }

}
