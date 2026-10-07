package com.portfoliotracker.service;


import com.portfoliotracker.exception.InvalidSymbolException;
import com.portfoliotracker.exception.PriceServiceUnavailableException;
import com.portfoliotracker.external.TwelveDataQuoteResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

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
        try {
            return restClient.get()
                    .uri("/quote?symbol={symbol}&apikey={key}", tickerSymbol, apiKey)
                    .retrieve()
                    .body(TwelveDataQuoteResponse.class);//returns the JSON back into the td java class
            //matching the fiels
        }catch (HttpClientErrorException.NotFound ex){
            throw new InvalidSymbolException("Sorry We could not find the Stock : " + tickerSymbol);
        }catch (RestClientException ex){
            throw new PriceServiceUnavailableException("Prices are currently not available, please try again later ");
        }
    }

    @Override
    public String lookUpCompanyName(String tickerSymbol) {
        TwelveDataQuoteResponse quote = fetchQuote(tickerSymbol);
        return quote.getName();
    }

    @Override
    //its fixed two exceptions both caught in one line.
    public Optional<BigDecimal> getCurrentPrice(String tickerSymbol) {
        try {
            TwelveDataQuoteResponse quote = fetchQuote(tickerSymbol);
            return  Optional.ofNullable(quote.getClose());
        }catch (InvalidSymbolException | PriceServiceUnavailableException ex){
            return Optional.empty();
        }

    }

}



