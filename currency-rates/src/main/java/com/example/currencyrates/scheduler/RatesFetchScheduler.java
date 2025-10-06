package com.example.currencyrates.scheduler;


import com.example.currencyrates.service.CurrencyService;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


@Component
public class RatesFetchScheduler {

    private static final Logger log = LoggerFactory.getLogger(RatesFetchScheduler.class);
    private final CurrencyService currencyService;

    public RatesFetchScheduler(CurrencyService currencyService) {
        this.currencyService = currencyService;
    }

    @Scheduled(cron = "${rates.fetch-cron}")
    public void fetchRates() {

        int saved = currencyService.fetchAndStoreLatest();
        log.info("Stored {} currency rates", saved);
    }

}
