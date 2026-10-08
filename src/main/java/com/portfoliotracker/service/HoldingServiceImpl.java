package com.portfoliotracker.service;

import com.portfoliotracker.dao.HoldingDao;
import com.portfoliotracker.model.Holding;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class HoldingServiceImpl implements HoldingService {

    private final HoldingDao holdingDao;

    public HoldingServiceImpl(HoldingDao holdingdao) {
        this.holdingDao = holdingdao;
    }

    @Override
    public List<Holding> getHoldingsByUserId(int userId) {
        return holdingDao.getHoldingsByUserId(userId);
    }

    @Override
    public Holding getHoldingById(int holdingId) {
        return holdingDao.findHoldingById(holdingId);
    }

    @Override
    public Holding addHolding(int userId, int stockId, BigDecimal quantity, BigDecimal purchasePrice)
            throws IllegalArgumentException {

        if (quantity == null || quantity.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than 0");
        }
        if (purchasePrice == null || purchasePrice.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Purchase must be greater than 0");
        }

        // Update the existing if it exists
        Holding existingHolding = holdingDao.findHoldingByUserAndStock(userId, stockId);
        if (existingHolding != null) {

            BigDecimal oldCost = existingHolding.getAveragePurchasePrice().multiply(existingHolding.getQuantity());
            BigDecimal newCost = purchasePrice.multiply(quantity);
            BigDecimal newQuantity = existingHolding.getQuantity().add(quantity);

            BigDecimal newAveragePrice = (oldCost.add(newCost)).divide(newQuantity, 4, RoundingMode.HALF_UP);

            return updateHolding(existingHolding.getHoldingId(), newQuantity, newAveragePrice);
        }

        Holding holding = new Holding();
        holding.setUserId(userId);
        holding.setStockId(stockId);
        holding.setQuantity(quantity);
        holding.setAveragePurchasePrice(purchasePrice);

        return holdingDao.createHolding(holding);
    }

    @Override
    public Holding updateHolding(int holdingId, BigDecimal quantity, BigDecimal averagePurchasePrice) {

        if (quantity == null || quantity.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than 0");
        }
        if (averagePurchasePrice == null || averagePurchasePrice.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Average purchase price must be greater than 0");
        }

        Holding updatedHolding = holdingDao.findHoldingById(holdingId);
        updatedHolding.setAveragePurchasePrice(averagePurchasePrice);
        updatedHolding.setQuantity(quantity);

        holdingDao.updateHolding(updatedHolding);

        return updatedHolding;
    }


    @Override
    public void removeHolding(int holdingId) {

        holdingDao.deleteHolding(holdingId);
    }


}