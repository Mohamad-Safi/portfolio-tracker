package com.portfoliotracker.dao;
import com.portfoliotracker.model.Stock;

import java.util.List;
import java.util.Optional;


public interface StockDao {
    //every stock ordered by the ticker symbol, return empty list if there is none
    List<Stock> getAllStocks();
    //stock with this id empty if it doesnt exist.
    Optional <Stock> getStockById(int id);
    //stock with the stock symbol
    Optional<Stock> getStockByTicker(String tickerSymbol);

    Stock addStock(Stock stock);

}
