package com.example.currencyrates.client;

import com.example.currencyrates.dto.LiveResponse;
import com.example.currencyrates.exception.ExternalServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Component
public class ExchangeRatesClient {

    private static final Logger log = LoggerFactory.getLogger(ExchangeRatesClient.class);

    private final WebClient webClient;
    private final String latestPath;
    private final String accessKey;

    public record LatestResponse(String base, String date, Map<String, BigDecimal> rates) {}

    public ExchangeRatesClient(WebClient ratesWebClient,
                               @Value("${rates.latest-path}") String latestPath,
                               @Value("${rates.access-key}") String accessKey) {
        this.webClient = ratesWebClient;
        this.latestPath = latestPath;
        this.accessKey = accessKey;
    }

    public LatestResponse fetchLatest() {
        try {
            LiveResponse live = webClient.get()
                    .uri(b -> b.path(latestPath)
                            .queryParam("access_key", accessKey)
                            .build())
                    .accept(org.springframework.http.MediaType.APPLICATION_JSON)
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, cr -> cr.bodyToMono(String.class)
                            .flatMap(body -> Mono.error(new ExternalServiceException("Rates API error: " + body))))
                    .bodyToMono(LiveResponse.class)
                    .timeout(Duration.ofSeconds(5))
                    .block();

            if (live == null || !live.success() || live.quotes() == null || live.quotes().isEmpty()) {
                throw new ExternalServiceException("Empty or invalid response from Rates API");
            }

            Map<String, BigDecimal> rates = new HashMap<>();
            String src = live.source();
            int prefixLen = src.length();
            for (var e : live.quotes().entrySet()) {
                rates.put(e.getKey().substring(prefixLen), e.getValue());
            }

            String isoDate = java.time.Instant.ofEpochSecond(live.timestamp())
                    .atOffset(java.time.ZoneOffset.UTC)
                    .toLocalDate()
                    .toString();

            return new LatestResponse(src, isoDate, rates);

        } catch (WebClientResponseException ex) {
            throw new ExternalServiceException("Failed to fetch rates from external service: " + ex.getMessage(), ex);
        } catch (Exception ex) {
            throw new ExternalServiceException("Unexpected error during rate fetch", ex);
        }
    }
}
