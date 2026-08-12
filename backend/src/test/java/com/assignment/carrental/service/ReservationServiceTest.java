package com.assignment.carrental.service;

import com.assignment.carrental.domain.*;
import com.assignment.carrental.dto.BookingRequest;
import com.assignment.carrental.repository.*;
import com.assignment.carrental.pricing.*;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ReservationServiceTest {

    private ReservationService service() {
        return new ReservationService(
                new InMemoryVehicleRepository(),
                new InMemoryReservationRepository(),
                new PricingService(List.of(
                        new SedanPricingStrategy(),
                        new VanPricingStrategy(),
                        new SuvPricingStrategy(),
                        new PickupTruckPricingStrategy()
                )));
    }

    private BookingRequest request(VehicleCategory category,
                                   String start,
                                   String end) {
        return new BookingRequest(
                "John",
                category,
                LocalDate.parse(start),
                LocalDate.parse(end),
                BigDecimal.ZERO,
                5);
    }

    @Test
    void shouldReserveVehicle() {
        var response = service().reserve(
                request(VehicleCategory.SEDAN, "2026-08-20", "2026-08-23"));

        assertNotNull(response.reservationId());
        assertEquals(VehicleCategory.SEDAN, response.category());
        assertEquals("ACTIVE", response.status());
        assertEquals(new BigDecimal("60.00"), response.totalAmount());
    }

    @Test
    void shouldCancelReservation() {
        var service = service();
        var response = service.reserve(
                request(VehicleCategory.SEDAN, "2026-08-20", "2026-08-23"));

        var cancelled = service.cancel(response.reservationId());

        assertEquals("CANCELLED", cancelled.status());
    }

    @Test
    void shouldNotAllowInvalidDates() {
        var service = service();

        assertThrows(IllegalArgumentException.class, () ->
                service.reserve(
                        request(VehicleCategory.SEDAN,
                                "2026-08-23", "2026-08-20")));
    }

    @Test
    void shouldRejectOverlappingReservationsWhenFleetIsExhausted() {
        var service = service();

        service.reserve(request(VehicleCategory.SEDAN, "2026-08-20", "2026-08-23"));
        service.reserve(request(VehicleCategory.SEDAN, "2026-08-20", "2026-08-23"));

        assertThrows(IllegalStateException.class, () ->
                service.reserve(request(VehicleCategory.SEDAN, "2026-08-20", "2026-08-23")));
    }

    @Test
    void modificationShouldKeepExistingReservationWhenNewBookingCannotBeAllocated() {
        var service = service();

        var first = service.reserve(
                request(VehicleCategory.SEDAN, "2026-08-20", "2026-08-23"));
        service.reserve(
                request(VehicleCategory.SEDAN, "2026-08-20", "2026-08-23"));

        assertThrows(IllegalStateException.class, () ->
                service.modify(first.reservationId(),
                        request(VehicleCategory.SEDAN,
                                "2026-08-20", "2026-08-23")));
    }
}
