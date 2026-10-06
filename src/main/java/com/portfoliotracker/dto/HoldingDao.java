package com.portfoliotracker.dto;

import com.portfoliotracker.model.Holding;

import java.util.List;

public interface HoldingDao {

    Holding createHolding(Holding holding);

    List<Holding> getHoldingsByUserId(int userId);

    Holding findHoldingById(int holdingId);

    Holding findHoldingByUserAndStock(int userId, int stockId);

    void updateHolding(Holding holding);

    void deleteHolding(int holdingId);
}