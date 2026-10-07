package com.portfoliotracker.dao;


import com.portfoliotracker.mapper.StockMapper;
import com.portfoliotracker.model.Stock;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public class StockDaoImpl implements StockDao{

    private final JdbcTemplate jdbcTemplate;

    public StockDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Stock> getAllStocks() {
        final String SELECT_ALL_STOCKS = "SELECT stockId, tickerSymbol, companyName FROM stock ORDER BY tickerSymbol";
        return jdbcTemplate.query(SELECT_ALL_STOCKS, new StockMapper());
    }

    @Override
    public Optional<Stock> getStockById(int id) {
        final String SELECT_STOCK_BYID =
                "SELECT stockId, tickerSymbol, companyName FROM stock WHERE stockId =?";
        List<Stock> stocks = jdbcTemplate.query(SELECT_STOCK_BYID, new StockMapper(), id);
        return stocks.stream().findFirst();
    }

    @Override
    public Optional<Stock> getStockByTicker(String tickerSymbol) {
        final String STOCK_BY_SYMBOL = "SELECT stockId, tickerSymbol, companyName FROM stock WHERE tickerSymbol =? ";
        List<Stock> stocks = jdbcTemplate.query(STOCK_BY_SYMBOL, new StockMapper(), tickerSymbol);
        return stocks.stream().findFirst();
    }

    @Override
    @Transactional
    public Stock addStock(Stock stock) {
        final String INSERT_STOCK =
                "INSERT INTO stock (tickerSymbol, companyName) VALUES (?, ?)";
        jdbcTemplate.update(INSERT_STOCK, stock.getTickerSymbol(), stock.getCompanyName());
        int newId = jdbcTemplate.queryForObject("SELECT LAST_INSERT_ID()", Integer.class);
        stock.setStockId(newId);
        return  stock;
    }
}
