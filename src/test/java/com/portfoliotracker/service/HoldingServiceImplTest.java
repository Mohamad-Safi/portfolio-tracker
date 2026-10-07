package com.portfoliotracker.service;


import com.portfoliotracker.dao.db.HoldingDao;
import com.portfoliotracker.model.Holding;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


public class HoldingServiceImplTest {

    @Autowired
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

        List<Holding> expectedHoldings =
                Arrays.asList(holding1, holding2);

        when(holdingDao.getHoldingsByUserId(1))
                .thenReturn(expectedHoldings);

        List<Holding> actualHoldings =
                holdingService.getHoldingsByUserId(1);

        assertEquals(2, actualHoldings.size());
        assertEquals(expectedHoldings, actualHoldings);

        verify(holdingDao).getHoldingsByUserId(1);
    }

    @Test
    void getHoldingById() {

        Holding holding = new Holding();
        holding.setHoldingId(1);
        holding.setUserId(1);
        holding.setStockId(1);
        holding.setQuantity(new BigDecimal("10"));
        holding.setAveragePurchasePrice(new BigDecimal("150.00"));

        when(holdingDao.findHoldingById(1))
                .thenReturn(holding);

        Holding result = holdingService.getHoldingById(1);

        assertNotNull(result);
        assertEquals(1, result.getHoldingId());
        assertEquals(1, result.getUserId());
        assertEquals(1, result.getStockId());

        verify(holdingDao).findHoldingById(1);
    }

    @Test
    void addHolding() {

        when(holdingDao.createHolding(any(Holding.class)))
                .thenAnswer(invocation -> {
                    Holding holding = invocation.getArgument(0);
                    holding.setHoldingId(1);
                    return holding;
                });

        Holding result = holdingService.addHolding(
                1,
                1,
                new BigDecimal("10"),
                new BigDecimal("150.00")
        );

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
                () -> holdingService.addHolding(
                        1,
                        1,
                        BigDecimal.ZERO,
                        new BigDecimal("150.00")
                )
        );

        verify(holdingDao, never()).createHolding(any(Holding.class));
    }
    @Test
    void addHoldingWithNullQuantity() {

        assertThrows(
                IllegalArgumentException.class,
                () -> holdingService.addHolding(
                        1,
                        1,
                        null,
                        new BigDecimal("150.00")
                )
        );

        verify(holdingDao, never())
                .createHolding(any(Holding.class));
    }

    @Test
    void addHoldingWithInvalidPurchasePrice() {

        assertThrows(
                IllegalArgumentException.class,
                () -> holdingService.addHolding(
                        1,
                        1,
                        new BigDecimal("10"),
                        BigDecimal.ZERO
                )
        );

        verify(holdingDao, never())
                .createHolding(any(Holding.class));
    }

    @Test
    void updateHolding() {

        Holding existingHolding = new Holding();
        existingHolding.setHoldingId(1);
        existingHolding.setUserId(1);
        existingHolding.setStockId(1);
        existingHolding.setQuantity(new BigDecimal("10"));
        existingHolding.setAveragePurchasePrice(
                new BigDecimal("150.00"));

        when(holdingDao.findHoldingById(1))
                .thenReturn(existingHolding);

        Holding result = holdingService.updateHolding(
                1,
                new BigDecimal("20"),
                new BigDecimal("175.00")
        );

        assertEquals(0,
                new BigDecimal("20")
                        .compareTo(result.getQuantity()));

        assertEquals(0,
                new BigDecimal("175.00")
                        .compareTo(result.getAveragePurchasePrice()));

        verify(holdingDao).findHoldingById(1);
        verify(holdingDao).updateHolding(existingHolding);
    }

    @Test
    void updateHoldingWithInvalidQuantity() {

        assertThrows(
                IllegalArgumentException.class,
                () -> holdingService.updateHolding(
                        1,
                        BigDecimal.ZERO,
                        new BigDecimal("150.00")
                )
        );

        verify(holdingDao, never())
                .updateHolding(any(Holding.class));
    }

    @Test
    void updateHoldingWithInvalidAveragePurchasePrice() {

        assertThrows(
                IllegalArgumentException.class,
                () -> holdingService.updateHolding(
                        1,
                        new BigDecimal("10"),
                        BigDecimal.ZERO
                )
        );

        verify(holdingDao, never())
                .updateHolding(any(Holding.class));
    }

    @Test
    void removeHolding() {

        holdingService.removeHolding(1);

        verify(holdingDao).deleteHolding(1);
    }
}
