package com.assignment.carrental.pricing;

import com.assignment.carrental.domain.VehicleCategory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class SedanPricingStrategy implements PricingStrategy {

    @Override
    public VehicleCategory category() {
        return VehicleCategory.SEDAN;
    }

    @Override
    public BigDecimal calculate(PricingContext context) {
        BigDecimal dailyRate = context.durationDays() < 10
                ? new BigDecimal("20")
                : new BigDecimal("15");

        BigDecimal base = dailyRate.multiply(BigDecimal.valueOf(context.durationDays()));
        return PricingCalculator.money(
                PricingCalculator.applyDriverSurcharge(base, context.licenseYears()));
    }
}
