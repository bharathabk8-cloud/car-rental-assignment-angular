package com.assignment.carrental.pricing;

import java.math.BigDecimal;

public record PricingContext(
        long durationDays,
        BigDecimal dailyMileage,
        int licenseYears) {
}
