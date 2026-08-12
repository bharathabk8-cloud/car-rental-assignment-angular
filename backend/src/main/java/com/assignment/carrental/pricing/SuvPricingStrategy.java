package com.assignment.carrental.pricing;

import com.assignment.carrental.domain.VehicleCategory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class SuvPricingStrategy implements PricingStrategy {

    @Override
    public VehicleCategory category() {
        return VehicleCategory.SUV;
    }

    @Override
    public BigDecimal calculate(PricingContext context) {
        BigDecimal base = new BigDecimal("15")
                .multiply(BigDecimal.valueOf(context.durationDays()));

        BigDecimal mileage = new BigDecimal("0.50")
                .multiply(context.dailyMileage())
                .multiply(BigDecimal.valueOf(context.durationDays()));

        BigDecimal total = base.add(mileage);

        return PricingCalculator.money(
                PricingCalculator.applyDriverSurcharge(
                        total, context.licenseYears()));
    }
}
