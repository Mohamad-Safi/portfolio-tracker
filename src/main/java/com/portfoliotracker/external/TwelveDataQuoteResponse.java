package com.portfoliotracker.external;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.math.BigDecimal;


@JsonIgnoreProperties(ignoreUnknown = true)
public class TwelveDataQuoteResponse {

    //fields should match the json that gets returned from the api call
    private String symbol;//for symbol
    private String name;//for company name
    private BigDecimal close; //for price
    private String status; //status returned from json on error
    private String message;//message if there is error
    private Integer code;//twelvedata's error code on error replies


    public boolean isError(){
        return "error".equals(status);
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getClose() {
        return close;
    }

    public void setClose(BigDecimal close) {
        this.close = close;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }
}
