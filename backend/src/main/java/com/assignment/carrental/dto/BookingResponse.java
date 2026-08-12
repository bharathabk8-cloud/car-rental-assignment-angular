package com.assignment.carrental.dto;

import com.assignment.carrental.domain.VehicleCategory;

import java.math.BigDecimal;
import java.time.LocalDate;

public record BookingResponse(
        String reservationId,
        String customerName,
        String vehicleId,
        VehicleCategory category,
        LocalDate startDate,
        LocalDate endDate,
        BigDecimal totalAmount,
        String status) {
}
