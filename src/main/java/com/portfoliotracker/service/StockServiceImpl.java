package com.portfoliotracker.service;

import com.portfoliotracker.dao.StockDao;
import com.portfoliotracker.model.Stock;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class StockServiceImpl implements StockService{

    private final StockDao stockDao;
    public StockServiceImpl(StockDao stockDao){
        this.stockDao = stockDao;
    }

    @Override
    public List<Stock> getAllStocks() {
        return stockDao.getAllStocks();
    }

    @Override
    public Stock getStockById(int id) {

        throw  new UnsupportedOperationException("///");
    }

    @Override
    public Stock findOrCreateStock(String tickerSymbol) {

        throw new UnsupportedOperationException("not builr yet yet");
    }
}
