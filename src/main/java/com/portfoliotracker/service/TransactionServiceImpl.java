package com.portfoliotracker.service;

import com.portfoliotracker.dao.TransactionDao;
import com.portfoliotracker.model.Transaction;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class TransactionServiceImpl implements TransactionService {

    private final TransactionDao transactionDao;

    public TransactionServiceImpl(TransactionDao transactionDao) {
        this.transactionDao = transactionDao;
    }

    @Override
    public Transaction recordTransaction(
            int userId,
            int stockId,
            String transactionType,
            BigDecimal quantity,
            BigDecimal price) {

        if (!"BUY".equals(transactionType) &&
                !"SELL".equals(transactionType) &&
                !"UPDATE".equals(transactionType) &&
                !"REMOVE".equals(transactionType)) {

            throw new IllegalArgumentException("Invalid transaction type");
        }

        if (quantity == null || quantity.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero");
        }

        if (price == null || price.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Price must be greater than zero");
        }

        Transaction transaction = new Transaction();

        transaction.setUserId(userId);
        transaction.setStockId(stockId);
        transaction.setTransactionType(transactionType);
        transaction.setQuantity(quantity);
        transaction.setPrice(price);

        return transactionDao.createTransaction(transaction);
    }

    @Override
    public List<Transaction> getTransactionsByUserId(int userId) {
        return transactionDao.getTransactionsByUserId(userId);
    }
}