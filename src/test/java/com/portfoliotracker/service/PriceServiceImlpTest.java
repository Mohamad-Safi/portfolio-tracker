package com.portfoliotracker.service;

import com.portfoliotracker.exception.InvalidSymbolException;
import com.portfoliotracker.exception.PriceServiceUnavailableException;
import com.portfoliotracker.external.TwelveDataClient;
import com.portfoliotracker.external.TwelveDataQuoteResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PriceServiceImplTest {

    @Mock
    private TwelveDataClient twelveDataClient;

    private PriceServiceImpl priceService;

    @BeforeEach
    void setUp() {
        priceService = new PriceServiceImpl(twelveDataClient, 15);
    }

    private TwelveDataQuoteResponse quote(String symbol, String name, String close) {
        TwelveDataQuoteResponse reply = new TwelveDataQuoteResponse();
        reply.setSymbol(symbol);
        reply.setName(name);
        reply.setClose(close == null ? null : new BigDecimal(close));
        return reply;
    }

    @Test
    void getCurrentPrice_firstCall_fetchesFromApi() {
        when(twelveDataClient.getQuote("AAPL"))
                .thenReturn(quote("AAPL", "Apple Inc.", "190.00"));

        Optional<BigDecimal> result = priceService.getCurrentPrice("AAPL");

        assertTrue(result.isPresent());
        assertEquals(new BigDecimal("190.00"), result.get());
        verify(twelveDataClient, times(1)).getQuote("AAPL");
    }

    @Test
    void getCurrentPrice_secondCallWithinTtl_usesCacheNotApi() {
        when(twelveDataClient.getQuote("AAPL"))
                .thenReturn(quote("AAPL", "Apple Inc.", "190.00"));

        Optional<BigDecimal> first = priceService.getCurrentPrice("AAPL");
        Optional<BigDecimal> second = priceService.getCurrentPrice("AAPL");
        assertEquals(first, second);
        verify(twelveDataClient, times(1)).getQuote("AAPL");
    }

    @Test
    void getCurrentPrice_lowercaseThenUppercase_sharesOneCacheEntry() {
        when(twelveDataClient.getQuote("AAPL"))
                .thenReturn(quote("AAPL", "Apple Inc.", "190.00"));

        priceService.getCurrentPrice("aapl");
        priceService.getCurrentPrice("AAPL");

        verify(twelveDataClient, times(1)).getQuote("AAPL");
    }

    @Test
    void getCurrentPrice_expiredEntry_fetchesNewPrice() {
        PriceServiceImpl zeroCacheService = new PriceServiceImpl(twelveDataClient, 0);

        when(twelveDataClient.getQuote("AAPL"))
                .thenReturn(quote("AAPL", "Apple Inc.", "190.00"),
                        quote("AAPL", "Apple Inc.", "195.00"));

        Optional<BigDecimal> first = zeroCacheService.getCurrentPrice("AAPL");
        Optional<BigDecimal> second = zeroCacheService.getCurrentPrice("AAPL");

        assertEquals(new BigDecimal("190.00"), first.get());
        assertEquals(new BigDecimal("195.00"), second.get());
        verify(twelveDataClient, times(2)).getQuote("AAPL");
    }

    @Test
    void getCurrentPrice_fetchFailsButOldPriceExists_returnsOldPrice() {
        PriceServiceImpl zeroCacheService = new PriceServiceImpl(twelveDataClient, 0);

        when(twelveDataClient.getQuote("AAPL"))
                .thenReturn(quote("AAPL", "Apple Inc.", "190.00"))
                .thenThrow(new PriceServiceUnavailableException("Twelve Data is down"));

        zeroCacheService.getCurrentPrice("AAPL");
        Optional<BigDecimal> result = zeroCacheService.getCurrentPrice("AAPL");

        assertTrue(result.isPresent());
        assertEquals(new BigDecimal("190.00"), result.get());
    }

    @Test
    void getCurrentPrice_fetchFailsAndNothingCached_returnsEmpty() {
        when(twelveDataClient.getQuote("ZZZZ"))
                .thenThrow(new InvalidSymbolException("We could not find the stock: ZZZZ"));

        Optional<BigDecimal> result = priceService.getCurrentPrice("ZZZZ");

        assertTrue(result.isEmpty());
    }

    @Test
    void lookUpCompanyName_realTicker_returnsName() {
        when(twelveDataClient.getQuote("TSLA"))
                .thenReturn(quote("TSLA", "Tesla Inc", "250.00"));

        String name = priceService.lookUpCompanyName("TSLA");

        assertEquals("Tesla Inc", name);
    }

    @Test
    void lookUpCompanyName_fakeTicker_throwsInvalidSymbolException() {
        when(twelveDataClient.getQuote("ZZZZ"))
                .thenThrow(new InvalidSymbolException("We could not find the stock: ZZZZ"));

        assertThrows(InvalidSymbolException.class,
                () -> priceService.lookUpCompanyName("ZZZZ"));
    }
}