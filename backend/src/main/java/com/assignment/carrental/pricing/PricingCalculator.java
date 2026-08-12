package com.assignment.carrental.pricing;

import java.math.BigDecimal;
import java.math.RoundingMode;

public final class PricingCalculator {

    private static final BigDecimal DRIVER_SURCHARGE = new BigDecimal("0.10");

    private PricingCalculator() {
    }

    public static BigDecimal applyDriverSurcharge(
            BigDecimal amount, int licenseYears) {
        if (licenseYears < 3) {
            return amount.multiply(BigDecimal.ONE.add(DRIVER_SURCHARGE));
        }
        return amount;
    }

    public static BigDecimal money(BigDecimal amount) {
        return amount.setScale(2, RoundingMode.HALF_UP);
    }
}
