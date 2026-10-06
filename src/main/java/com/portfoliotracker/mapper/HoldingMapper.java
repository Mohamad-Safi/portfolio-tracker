package com.portfoliotracker.mapper;

import com.portfoliotracker.model.Holding;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class HoldingMapper implements RowMapper<Holding> {

    @Override
    public Holding mapRow(ResultSet rs, int rowNum) throws SQLException {
        Holding holding = new Holding();

        holding.setHoldingId(rs.getInt("holdingId"));
        holding.setUserId(rs.getInt("userId"));
        holding.setStockId(rs.getInt("stockId"));
        holding.setQuantity(rs.getBigDecimal("quantity"));
        holding.setAveragePurchasePrice(
                rs.getBigDecimal("averagePurchasePrice")
        );

        return holding;
    }
}