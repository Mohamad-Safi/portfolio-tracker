package com.portfoliotracker.mapper;

import com.portfoliotracker.model.Transaction;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class TransactionMapper implements RowMapper<Transaction> {

    @Override
    public Transaction mapRow(ResultSet rs, int rowNum) throws SQLException {

        Transaction transaction = new Transaction();

        transaction.setTransactionId(rs.getInt("transactionId"));
        transaction.setUserId(rs.getInt("userId"));
        transaction.setStockId(rs.getInt("stockId"));
        transaction.setTransactionType(rs.getString("transactionType"));
        transaction.setQuantity(rs.getBigDecimal("quantity"));
        transaction.setPrice(rs.getBigDecimal("price"));
        transaction.setTransactionDate(rs.getTimestamp("transactionDate").toLocalDateTime());

        return transaction;
    }
}