package com.portfoliotracker.service;


import com.portfoliotracker.external.TwelveDataQuoteResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.util.Optional;

@Service
public class PriceServiceImpl implements PriceService{

    //bean for restclient already created in appconfig and handed in here in the constructor
    private final RestClient restClient;
    private final String apiKey;

    public PriceServiceImpl(RestClient restClient, @Value("${twelvedata.api-key}") String apiKey){
        this.restClient = restClient;
        this.apiKey = apiKey;
    }

    //ask twelve for quote and return its json as a java object.
    private TwelveDataQuoteResponse fetchQuote(String tickerSymbol) {
        //get request but to the api
        return restClient.get()
                .uri("/quote?symbol={symbol}&apikey={key}", tickerSymbol, apiKey)
                .retrieve()
                .body(TwelveDataQuoteResponse.class);//returns the JSON back into the td java class
        //matching the fiels
    }

    @Override
    public String lookUpCompanyName(String tickerSymbol) {
        TwelveDataQuoteResponse quote = fetchQuote(tickerSymbol);
        return quote.getName();
    }

    @Override
    public Optional<BigDecimal> getCurrentPrice(String tickerSymbol) {
        TwelveDataQuoteResponse quote = fetchQuote(tickerSymbol);
        return  Optional.ofNullable(quote.getClose());
    }

}



