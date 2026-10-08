package com.portfoliotracker.controller;

import com.portfoliotracker.model.Transaction;
import com.portfoliotracker.security.AuthInterceptor;
import com.portfoliotracker.service.TransactionService;

import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping
    public List<Transaction> getTransactions(HttpSession session) {

        int userId = (Integer) session.getAttribute(AuthInterceptor.USER_ID);

        return transactionService.getTransactionsByUserId(userId);
    }
}