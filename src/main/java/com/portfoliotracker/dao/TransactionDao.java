package com.portfoliotracker.dao;

import com.portfoliotracker.model.Transaction;
import java.util.List;

public interface TransactionDao {

    Transaction createTransaction(Transaction transaction);

    List<Transaction> getTransactionsByUserId(int userId);
}