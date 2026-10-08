package com.portfoliotracker.dao;

import com.portfoliotracker.dao.TransactionDao;
import com.portfoliotracker.mapper.TransactionMapper;
import com.portfoliotracker.model.Transaction;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

@Repository
public class TransactionDaoImpl implements TransactionDao {

    private final JdbcTemplate jdbcTemplate;

    public TransactionDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Transaction createTransaction(Transaction transaction) {

        String sql = """
                INSERT INTO transaction_history
                (userId, stockId, transactionType, quantity, price)
                VALUES (?, ?, ?, ?, ?)
                """;

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {

            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            ps.setInt(1, transaction.getUserId());
            ps.setInt(2, transaction.getStockId());
            ps.setString(3, transaction.getTransactionType());
            ps.setBigDecimal(4, transaction.getQuantity());
            ps.setBigDecimal(5, transaction.getPrice());

            return ps;

        }, keyHolder);

        transaction.setTransactionId(keyHolder.getKey().intValue());

        return transaction;
    }

    @Override
    public List<Transaction> getTransactionsByUserId(int userId) {

        String sql = """
                SELECT * FROM transaction_history
                WHERE userId = ?
                ORDER BY transactionDate DESC, transactionId DESC
                """;

        return jdbcTemplate.query(sql, new TransactionMapper(), userId);
    }
}