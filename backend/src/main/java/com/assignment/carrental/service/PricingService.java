package com.assignment.carrental.service;

import com.assignment.carrental.domain.VehicleCategory;
import com.assignment.carrental.dto.PriceOption;
import com.assignment.carrental.pricing.PricingContext;
import com.assignment.carrental.pricing.PricingStrategy;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class PricingService {

    private final Map<VehicleCategory, PricingStrategy> strategies;

    public PricingService(List<PricingStrategy> pricingStrategies) {
        this.strategies = pricingStrategies.stream()
                .collect(Collectors.toUnmodifiableMap(
                        PricingStrategy::category,
                        Function.identity()));
    }

    public List<PriceOption> getOptions(
            LocalDate startDate,
            LocalDate endDate,
            BigDecimal dailyMileage,
            int licenseYears) {

        long durationDays = ChronoUnit.DAYS.between(startDate, endDate);

        PricingContext context =
                new PricingContext(durationDays, dailyMileage, licenseYears);

        return strategies.values().stream()
                .map(strategy -> new PriceOption(
                        strategy.category(),
                        durationDays,
                        strategy.calculate(context)))
                .sorted(Comparator.comparing(PriceOption::totalAmount))
                .toList();
    }

    public BigDecimal calculate(
            VehicleCategory category,
            LocalDate startDate,
            LocalDate endDate,
            BigDecimal dailyMileage,
            int licenseYears) {

        long durationDays = ChronoUnit.DAYS.between(startDate, endDate);
        PricingStrategy strategy = strategies.get(category);

        if (strategy == null) {
            throw new IllegalArgumentException("Unsupported vehicle category: " + category);
        }

        return strategy.calculate(
                new PricingContext(durationDays, dailyMileage, licenseYears));
    }
}
