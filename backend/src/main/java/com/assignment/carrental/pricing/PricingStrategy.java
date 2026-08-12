package com.assignment.carrental.pricing;

import com.assignment.carrental.domain.VehicleCategory;

import java.math.BigDecimal;

public interface PricingStrategy {
    VehicleCategory category();
    BigDecimal calculate(PricingContext context);
}
