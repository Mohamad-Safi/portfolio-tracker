package com.portfoliotracker.mapper;

import com.portfoliotracker.model.Stock;

import org.springframework.jdbc.core.RowMapper;
import java.sql.ResultSet;
import java.sql.SQLException;

public class StockMapper implements RowMapper<Stock>{

    @Override
    public Stock mapRow(ResultSet rs, int rowNum) throws SQLException{
        Stock stock = new Stock();
        stock.setStockId(rs.getInt("stockId"));
        stock.setTickerSymbol(rs.getString("tickerSymbol"));
        stock.setCompanyName(rs.getString("companyName"));
        return stock;

    }
}
