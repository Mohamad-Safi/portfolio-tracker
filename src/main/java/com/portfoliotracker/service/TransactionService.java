package com.portfoliotracker.service;

import com.portfoliotracker.model.Transaction;
import java.math.BigDecimal;
import java.util.List;

public interface TransactionService {

    Transaction recordTransaction(
            int userId,
            int stockId,
            String transactionType,
            BigDecimal quantity,
            BigDecimal price);

    List<Transaction> getTransactionsByUserId(int userId);
}