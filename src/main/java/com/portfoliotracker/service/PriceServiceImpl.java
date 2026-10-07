package com.portfoliotracker.service;


import com.portfoliotracker.exception.InvalidSymbolException;
import com.portfoliotracker.exception.PriceServiceUnavailableException;
import com.portfoliotracker.external.TwelveDataClient;
import com.portfoliotracker.external.TwelveDataQuoteResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class PriceServiceImpl implements PriceService{

    private final TwelveDataClient twelveDataClient;
    private final long cacheMinutes;

    //ticker and its cached price
    private final Map<String, CachedPrice> cache = new ConcurrentHashMap<>();
    private record CachedPrice(BigDecimal price, LocalDateTime fetchedAt){}


    public PriceServiceImpl(TwelveDataClient twelveDataClient,
                            @Value("${pricing.cache-minutes}") long cacheMinutes){
        this.twelveDataClient = twelveDataClient;
        this.cacheMinutes = cacheMinutes;
    }

    //helper method to check price.
    private boolean isFresh(CachedPrice entry){

        LocalDateTime expiresAt = entry.fetchedAt().plusMinutes(cacheMinutes);
        return LocalDateTime.now().isBefore(expiresAt);
    }

    @Override
    public Optional<BigDecimal> getCurrentPrice(String tickerSymbol) {
        String ticker = tickerSymbol.toUpperCase();
        CachedPrice cached = cache.get(ticker);

        if (cached!=null && isFresh(cached)){
            return Optional.of(cached.price());
        }
        try {
            TwelveDataQuoteResponse quote = twelveDataClient.getQuote(ticker);
            BigDecimal price = quote.getClose();
            if (price != null){
                cache.put(ticker, new CachedPrice(price, LocalDateTime.now()));
                return Optional.of(price);
            }
        }catch (InvalidSymbolException | PriceServiceUnavailableException ex){
            //fetching failed
        }
        if (cached != null){
            return Optional.of(cached.price());
        }else {
            return Optional.empty();
        }

    }

    @Override
    public String lookUpCompanyName(String tickerSymbol) {
        TwelveDataQuoteResponse quote = twelveDataClient.getQuote(tickerSymbol);
        return quote.getName();
    }

}



