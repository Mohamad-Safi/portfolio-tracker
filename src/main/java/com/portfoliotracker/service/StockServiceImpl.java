package com.portfoliotracker.service;

import com.portfoliotracker.dao.StockDao;
import com.portfoliotracker.exception.StockNotFoundException;
import com.portfoliotracker.model.Stock;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


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
    public Stock getStockById(int stockId) {

        Optional<Stock> result = stockDao.getStockById(stockId);
        if (result.isEmpty()){
            throw new StockNotFoundException("No stock found with id : "+ stockId);
        }
        return result.get();
    }

    @Override
    public Stock findOrCreateStock(String tickerSymbol) {

        throw new UnsupportedOperationException("not builr yet yet");
    }
}
