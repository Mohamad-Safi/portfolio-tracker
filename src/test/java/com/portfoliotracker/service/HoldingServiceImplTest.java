package com.portfoliotracker.service;


import com.portfoliotracker.dao.HoldingDao;
import com.portfoliotracker.exception.HoldingNotFoundException;
import com.portfoliotracker.model.Holding;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


public class HoldingServiceImplTest {


    HoldingService holdingService;
    HoldingDao holdingDao;

    @BeforeEach
    void setUp() {
        holdingDao = mock(HoldingDao.class);
        holdingService = new HoldingServiceImpl(holdingDao);
    }

    @Test
    void getHoldingsByUserId() {

        Holding holding1 = new Holding();
        holding1.setHoldingId(1);
        holding1.setUserId(1);
        holding1.setStockId(1);

        Holding holding2 = new Holding();
        holding2.setHoldingId(2);
        holding2.setUserId(1);
        holding2.setStockId(2);

        List<Holding> expectedHoldings = Arrays.asList(holding1, holding2);

        when(holdingDao.getHoldingsByUserId(1)).thenReturn(expectedHoldings);

        List<Holding> actualHoldings = holdingService.getHoldingsByUserId(1);

        assertEquals(2, actualHoldings.size());
        assertEquals(expectedHoldings, actualHoldings);

        verify(holdingDao).getHoldingsByUserId(1);
    }

    @Test
    void getHoldingById() {

        int userId = 1;
        int holdingId = 10;

        Holding holding = new Holding();
        holding.setHoldingId(holdingId);
        holding.setUserId(userId);

        when(holdingDao.findHoldingById(holdingId)).thenReturn(holding);

        Holding result = holdingService.getHoldingById(userId, holdingId);

        assertEquals(holdingId, result.getHoldingId());
        assertEquals(userId, result.getUserId());
    }

    @Test
    void addHolding() {

        when(holdingDao.createHolding(any(Holding.class)))
                .thenAnswer(invocation -> {
                    Holding holding = invocation.getArgument(0);
                    holding.setHoldingId(1);
                    return holding;
                });

        Holding result = holdingService.addHolding(1, 1, new BigDecimal("10"),
                                                    new BigDecimal("150.00"));

        assertNotNull(result);
        assertEquals(1, result.getHoldingId());
        assertEquals(1, result.getUserId());
        assertEquals(1, result.getStockId());

        assertEquals(0, new BigDecimal("10").compareTo(result.getQuantity()));

        assertEquals(0, new BigDecimal("150.00").compareTo(result.getAveragePurchasePrice()));

        verify(holdingDao).createHolding(any(Holding.class));
    }

    @Test
    void addHoldingWithInvalidQuantity() {

        assertThrows(
                IllegalArgumentException.class,
                () -> holdingService.addHolding(1, 1, BigDecimal.ZERO, new BigDecimal("150.00")));

        verify(holdingDao, never()).createHolding(any(Holding.class));
    }
    @Test
    void addHoldingWithNullQuantity() {

        assertThrows(
                IllegalArgumentException.class,
                () -> holdingService.addHolding(1, 1, null, new BigDecimal("150.00")));

        verify(holdingDao, never()).createHolding(any(Holding.class));
    }

    @Test
    void addHoldingWithInvalidPurchasePrice() {

        assertThrows(
                IllegalArgumentException.class,
                () -> holdingService.addHolding(1, 1, new BigDecimal("10"), BigDecimal.ZERO));

        verify(holdingDao, never())
                .createHolding(any(Holding.class));
    }

    @Test
    void updateHoldingTest() {

        int userId = 1;
        int holdingId = 10;

        Holding holding = new Holding();
        holding.setHoldingId(holdingId);
        holding.setUserId(userId);

        when(holdingDao.findHoldingById(holdingId)).thenReturn(holding);

        BigDecimal quantity = new BigDecimal("20");
        BigDecimal price = new BigDecimal("150");

        Holding result = holdingService.updateHolding(userId, holdingId, quantity, price);

        assertEquals(quantity, result.getQuantity());
        assertEquals(price, result.getAveragePurchasePrice());

        verify(holdingDao).updateHolding(holding);
    }

    @Test
    void updateHoldingWithInvalidQuantity() {

        assertThrows(
                IllegalArgumentException.class,
                () -> holdingService.updateHolding(1, 1, BigDecimal.ZERO,
                                                    new BigDecimal("150.00")));

        verify(holdingDao, never()).updateHolding(any(Holding.class));
    }

    @Test
    void updateHoldingWithInvalidAveragePurchasePrice() {

        assertThrows(
                IllegalArgumentException.class,
                () -> holdingService.updateHolding(1, 1, new BigDecimal("10"), BigDecimal.ZERO));

        verify(holdingDao, never()).updateHolding(any(Holding.class));
    }

    @Test
    void removeHoldingTest() {

        int userId = 1;
        int holdingId = 10;

        Holding holding = new Holding();
        holding.setHoldingId(holdingId);
        holding.setUserId(userId);

        when(holdingDao.findHoldingById(holdingId)).thenReturn(holding);

        holdingService.removeHolding(userId, holdingId);

        verify(holdingDao).deleteHolding(holdingId);
    }

    @Test
    void cannotDeleteAnotherUsersHolding() {

        Holding holding = new Holding();
        holding.setHoldingId(10);
        holding.setUserId(2);

        when(holdingDao.findHoldingById(10)).thenReturn(holding);

        assertThrows(HoldingNotFoundException.class, () ->
                holdingService.removeHolding(1, 10));

        verify(holdingDao, never()).deleteHolding(10);
    }

    @Test
    void cannotViewAnotherUsersHolding() {

        Holding holding = new Holding();
        holding.setHoldingId(10);
        holding.setUserId(2);

        when(holdingDao.findHoldingById(10)).thenReturn(holding);

        assertThrows(HoldingNotFoundException.class, () ->
                holdingService.getHoldingById(1, 10)
        );
    }
}