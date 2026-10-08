package com.portfoliotracker.service;

import com.portfoliotracker.model.Holding;

import java.math.BigDecimal;
import java.util.List;

public interface HoldingService {

    Holding addHolding(int userId, int stockId, BigDecimal quantity, BigDecimal purchasePrice);

    List<Holding> getHoldingsByUserId(int userId);

    Holding getHoldingById(int userId, int holdingId);

    Holding updateHolding(int userId, int holdingId, BigDecimal quantity, BigDecimal averagePurchasePrice);

    void removeHolding(int userId, int holdingId);
}