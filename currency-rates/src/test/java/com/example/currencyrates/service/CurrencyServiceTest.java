package com.example.currencyrates.service;

import com.example.currencyrates.client.ExchangeRatesClient;
import com.example.currencyrates.domain.CurrencyRate;
import com.example.currencyrates.exception.ExternalServiceException;
import com.example.currencyrates.repository.CurrencyRateRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class CurrencyServiceTest {

    @Mock
    private CurrencyRateRepository currencyRateRepository;

    @Mock
    private ExchangeRatesClient exchangeRatesClient;

    @InjectMocks
    private CurrencyService currencyService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("fetchAndStoreLatest should persist all rates returned by client")
    void shouldFetchAndPersistRatesSuccessfully() {
        // Arrange
        var latestResponse = new ExchangeRatesClient.LatestResponse(
                "USD", "2025-10-06",
                Map.of("EUR", BigDecimal.valueOf(0.92), "GBP", BigDecimal.valueOf(0.81))
        );

        when(exchangeRatesClient.fetchLatest()).thenReturn(latestResponse);

        // Act
        int savedCount = currencyService.fetchAndStoreLatest();

        // Assert
        ArgumentCaptor<List<CurrencyRate>> captor = ArgumentCaptor.forClass(List.class);
        verify(currencyRateRepository, times(1)).saveAll(captor.capture());

        List<CurrencyRate> savedRates = captor.getValue();
        assertThat(savedRates).hasSize(2);
        assertThat(savedRates)
                .extracting(CurrencyRate::getQuoteCurrency)
                .containsExactlyInAnyOrder("EUR", "GBP");

        assertThat(savedCount).isEqualTo(2);
    }

    @Test
    @DisplayName("fetchAndStoreLatest should throw ExternalServiceException if API fails")
    void shouldThrowExternalServiceExceptionOnApiFailure() {
        // Arrange
        when(exchangeRatesClient.fetchLatest()).thenThrow(new ExternalServiceException("API error"));

        // Act + Assert
        assertThatThrownBy(() -> currencyService.fetchAndStoreLatest())
                .isInstanceOf(ExternalServiceException.class)
                .hasMessageContaining("API error");

        verify(currencyRateRepository, never()).saveAll(any());
    }

    @Test
    @DisplayName("fetchAndStoreLatest should call repository.saveAll exactly once")
    void shouldCallRepositorySaveAllOnce() {
        // Arrange
        var latestResponse = new ExchangeRatesClient.LatestResponse(
                "USD", "2025-10-06",
                Map.of("EUR", BigDecimal.ONE)
        );
        when(exchangeRatesClient.fetchLatest()).thenReturn(latestResponse);

        // Act
        currencyService.fetchAndStoreLatest();

        // Assert
        verify(currencyRateRepository, times(1)).saveAll(anyList());
    }

    @Test
    void fetchAndStoreLatest_ShouldThrowException_OnExternalFailure() {
        // Arrange
        when(exchangeRatesClient.fetchLatest()).thenThrow(new IllegalStateException("Rates API error: 502 Bad Gateway"));

        // Act + Assert
        assertThatThrownBy(() -> currencyService.fetchAndStoreLatest())
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Rates API error");

        verify(exchangeRatesClient, times(1)).fetchLatest();
        verifyNoInteractions(currencyRateRepository);
    }

}


