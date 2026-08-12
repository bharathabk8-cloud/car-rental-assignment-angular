package com.assignment.carrental.pricing;

import com.assignment.carrental.dto.PriceOption;
import com.assignment.carrental.service.PricingService;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PricingServiceTest {

    private final PricingService pricingService = new PricingService(List.of(
            new SedanPricingStrategy(),
            new VanPricingStrategy(),
            new SuvPricingStrategy(),
            new PickupTruckPricingStrategy()
    ));

    @Test
    void shouldCalculateOptionsAndSortAscending() {
        List<PriceOption> options = pricingService.getOptions(
                LocalDate.of(2026, 8, 20),
                LocalDate.of(2026, 8, 23),
                new BigDecimal("100"),
                5);

        assertEquals(4, options.size());
        assertEquals("SEDAN", options.get(0).category().name());
        assertEquals(new BigDecimal("60.00"), options.get(0).totalAmount());
        assertEquals(new BigDecimal("72.60"), options.get(1).totalAmount());
        assertEquals(new BigDecimal("90.00"), options.get(2).totalAmount());
        assertEquals(new BigDecimal("195.00"), options.get(3).totalAmount());
    }

    @Test
    void sedanShouldUseDiscountedRateAtTenDays() {
        var options = pricingService.getOptions(
                LocalDate.of(2026, 8, 1),
                LocalDate.of(2026, 8, 11),
                BigDecimal.ZERO,
                5);

        var sedan = options.stream()
                .filter(o -> o.category().name().equals("SEDAN"))
                .findFirst()
                .orElseThrow();

        assertEquals(new BigDecimal("150.00"), sedan.totalAmount());
    }

    @Test
    void inexperiencedDriverShouldPayTenPercentSurcharge() {
        var options = pricingService.getOptions(
                LocalDate.of(2026, 8, 20),
                LocalDate.of(2026, 8, 23),
                BigDecimal.ZERO,
                2);

        var sedan = options.stream()
                .filter(o -> o.category().name().equals("SEDAN"))
                .findFirst()
                .orElseThrow();

        assertEquals(new BigDecimal("66.00"), sedan.totalAmount());
    }
}
