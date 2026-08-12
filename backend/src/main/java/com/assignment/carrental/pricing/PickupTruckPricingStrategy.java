package com.assignment.carrental.pricing;

import com.assignment.carrental.domain.VehicleCategory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class PickupTruckPricingStrategy implements PricingStrategy {

    @Override
    public VehicleCategory category() {
        return VehicleCategory.PICKUP_TRUCK;
    }

    @Override
    public BigDecimal calculate(PricingContext context) {
        BigDecimal base = new BigDecimal("30")
                .multiply(BigDecimal.valueOf(context.durationDays()));

        return PricingCalculator.money(
                PricingCalculator.applyDriverSurcharge(
                        base, context.licenseYears()));
    }
}
