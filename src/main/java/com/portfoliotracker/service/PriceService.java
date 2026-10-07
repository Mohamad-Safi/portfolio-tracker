package com.portfoliotracker.service;

import java.math.BigDecimal;
import java.util.Optional;

public interface PriceService {


    //returns company name or throws invalid symbol or price unavailable exception
    String lookUpCompanyName(String tickerSymbol);
    //method gives the current live twelvedata price or the cached price, because of limited calls
    //if api is down or the price isnt stored then returns an empty optional java object
    Optional<BigDecimal> getCurrentPrice(String tickerSymbol);

}
