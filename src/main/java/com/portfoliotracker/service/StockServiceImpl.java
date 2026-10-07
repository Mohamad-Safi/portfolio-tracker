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
    private final PriceService priceService;
    public StockServiceImpl(StockDao stockDao, PriceService priceService){
        this.stockDao = stockDao;
        this.priceService = priceService;
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
        String ticker = tickerSymbol.trim().toUpperCase();
        Optional<Stock> existingStock = stockDao.getStockByTicker(ticker);
        if (existingStock.isPresent()){
            return existingStock.get();
        }
        String companyName = priceService.lookUpCompanyName(ticker);
        Stock newStock = new Stock();
        newStock.setTickerSymbol(ticker);
        newStock.setCompanyName(companyName);
        stockDao.addStock(newStock);
        return newStock;

    }
}
