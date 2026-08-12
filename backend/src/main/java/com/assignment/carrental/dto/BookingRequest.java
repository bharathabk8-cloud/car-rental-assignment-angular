package com.assignment.carrental.dto;

import com.assignment.carrental.domain.VehicleCategory;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;
import java.time.LocalDate;

public record BookingRequest(
        @NotBlank String customerName,
        @NotNull VehicleCategory category,
        @NotNull LocalDate startDate,
        @NotNull LocalDate endDate,
        @NotNull @DecimalMin(value = "0.0", inclusive = true) BigDecimal dailyMileage,
        @PositiveOrZero int licenseYears) {
}
