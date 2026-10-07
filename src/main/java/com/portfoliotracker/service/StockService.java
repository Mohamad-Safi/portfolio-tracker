package com.portfoliotracker.service;
import com.portfoliotracker.model.Stock;
import java.util.List;


public interface StockService {
    List<Stock> getAllStocks();//return all stocks
    Stock getStockById(int id);//if stock not found throw exception
    Stock findOrCreateStock(String tickerSymbol);

}
