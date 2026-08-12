package com.assignment.carrental.pricing;

import com.assignment.carrental.domain.VehicleCategory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class VanPricingStrategy implements PricingStrategy {

    @Override
    public VehicleCategory category() {
        return VehicleCategory.VAN;
    }

    @Override
    public BigDecimal calculate(PricingContext context) {
        BigDecimal base = new BigDecimal("22")
                .multiply(BigDecimal.valueOf(context.durationDays()));

        BigDecimal withCleaning = base.multiply(new BigDecimal("1.10"));

        return PricingCalculator.money(
                PricingCalculator.applyDriverSurcharge(
                        withCleaning, context.licenseYears()));
    }
}
