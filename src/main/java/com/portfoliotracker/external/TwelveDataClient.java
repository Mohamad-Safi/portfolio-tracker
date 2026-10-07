package com.portfoliotracker.external;


import com.portfoliotracker.exception.InvalidSymbolException;
import com.portfoliotracker.exception.PriceServiceUnavailableException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Component
public class TwelveDataClient {



    private final RestClient restClient;
    private final String apiKey;

    public TwelveDataClient(RestClient restClient,
                            @Value("${twelvedata.api-key}") String apiKey) {
        this.restClient = restClient;
        this.apiKey = apiKey;
    }

    // Fetches one quote. Throws InvalidSymbolException for an unknown ticker,
    // or PriceServiceUnavailableException for any other failure.
    public TwelveDataQuoteResponse getQuote(String tickerSymbol) {
        try {
            return restClient.get()
                    .uri("/quote?symbol={symbol}&apikey={key}", tickerSymbol, apiKey)
                    .retrieve()
                    .body(TwelveDataQuoteResponse.class);
        } catch (HttpClientErrorException.NotFound ex) {
            throw new InvalidSymbolException("We could not find the stock: " + tickerSymbol);
        } catch (RestClientException ex) {
            throw new PriceServiceUnavailableException("Prices are temporarily unavailable. Please try again later.");
        }
    }
}
