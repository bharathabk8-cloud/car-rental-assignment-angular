package com.assignment.carrental.domain;

import java.math.BigDecimal;
import java.time.LocalDate;

public record Reservation(
        String id,
        String customerName,
        String vehicleId,
        VehicleCategory category,
        LocalDate startDate,
        LocalDate endDate,
        BigDecimal totalAmount,
        boolean active) {

    public Reservation withDetails(
            String newVehicleId,
            VehicleCategory newCategory,
            LocalDate newStartDate,
            LocalDate newEndDate,
            BigDecimal newTotalAmount) {
        return new Reservation(
                id,
                customerName,
                newVehicleId,
                newCategory,
                newStartDate,
                newEndDate,
                newTotalAmount,
                true);
    }

    public Reservation cancelled() {
        return new Reservation(
                id, customerName, vehicleId, category,
                startDate, endDate, totalAmount, false);
    }
}
