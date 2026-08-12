package com.assignment.carrental.dto;

import com.assignment.carrental.domain.VehicleCategory;

import java.math.BigDecimal;

public record PriceOption(
        VehicleCategory category,
        long durationDays,
        BigDecimal totalAmount) {
}
