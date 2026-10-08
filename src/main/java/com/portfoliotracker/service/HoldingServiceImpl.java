package com.portfoliotracker.service;

import com.portfoliotracker.dao.HoldingDao;
import com.portfoliotracker.exception.HoldingNotFoundException;
import com.portfoliotracker.model.Holding;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class HoldingServiceImpl implements HoldingService {

    private final HoldingDao holdingDao;
    private final TransactionService transactionService;

    public HoldingServiceImpl(HoldingDao holdingdao, TransactionService transactionService) {
        this.holdingDao = holdingdao;
        this.transactionService = transactionService;
    }

    @Override
    public List<Holding> getHoldingsByUserId(int userId) {
        return holdingDao.getHoldingsByUserId(userId);
    }

    @Override
    public Holding getHoldingById(int userId, int holdingId) {
        return getUserHolding(userId, holdingId);
    }

    @Override
    @Transactional
    public Holding addHolding(int userId, int stockId,
                              BigDecimal quantity, BigDecimal purchasePrice) {

        if (quantity == null || quantity.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than 0");
        }

        if (purchasePrice == null || purchasePrice.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Purchase price must be greater than 0");
        }

        Holding existingHolding = holdingDao.findHoldingByUserAndStock(userId, stockId);
        Holding result;

        if (existingHolding != null) {

            BigDecimal oldCost = existingHolding.getAveragePurchasePrice().multiply(existingHolding.getQuantity());
            BigDecimal newCost = purchasePrice.multiply(quantity);
            BigDecimal newQuantity = existingHolding.getQuantity().add(quantity);
            BigDecimal newAveragePrice = oldCost.add(newCost).divide(newQuantity, 4, RoundingMode.HALF_UP);

            existingHolding.setQuantity(newQuantity);
            existingHolding.setAveragePurchasePrice(newAveragePrice);
            holdingDao.updateHolding(existingHolding);

            result = existingHolding;

        } else {

            Holding holding = new Holding();
            holding.setUserId(userId);
            holding.setStockId(stockId);
            holding.setQuantity(quantity);
            holding.setAveragePurchasePrice(purchasePrice);

            result = holdingDao.createHolding(holding);
        }

        transactionService.recordTransaction(
                userId,
                stockId,
                "BUY",
                quantity,
                purchasePrice);

        return result;
    }

    @Override
    @Transactional
    public Holding updateHolding(int userId, int holdingId, BigDecimal quantity, BigDecimal averagePurchasePrice) {

        if (quantity == null || quantity.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than 0");
        }

        if (averagePurchasePrice == null || averagePurchasePrice.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Average purchase price must be greater than 0");
        }

        Holding updatedHolding = getUserHolding(userId, holdingId);

        updatedHolding.setAveragePurchasePrice(averagePurchasePrice);
        updatedHolding.setQuantity(quantity);

        holdingDao.updateHolding(updatedHolding);

        transactionService.recordTransaction(
                userId,
                updatedHolding.getStockId(),
                "UPDATE",
                quantity,
                averagePurchasePrice);

        return updatedHolding;
    }


    @Override
    @Transactional
    public void removeHolding(int userId, int holdingId) {

        Holding holding = getUserHolding(userId, holdingId);

        holdingDao.deleteHolding(holdingId);

        transactionService.recordTransaction(
                userId,
                holding.getStockId(),
                "REMOVE",
                holding.getQuantity(),
                holding.getAveragePurchasePrice()
        );
    }

    private Holding getUserHolding(int userId, int holdingId) {

        Holding holding = holdingDao.findHoldingById(holdingId);

        if (holding == null || holding.getUserId() != userId) {
            throw new HoldingNotFoundException("Holding not found");
        }

        return holding;
    }


}