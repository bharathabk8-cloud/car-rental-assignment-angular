package com.assignment.carrental.service;

import com.assignment.carrental.domain.Reservation;
import com.assignment.carrental.domain.Vehicle;
import com.assignment.carrental.domain.VehicleCategory;
import com.assignment.carrental.dto.BookingRequest;
import com.assignment.carrental.dto.BookingResponse;
import com.assignment.carrental.repository.ReservationRepository;
import com.assignment.carrental.repository.VehicleRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class ReservationService {

    private final VehicleRepository vehicleRepository;
    private final ReservationRepository reservationRepository;
    private final PricingService pricingService;

    public ReservationService(
            VehicleRepository vehicleRepository,
            ReservationRepository reservationRepository,
            PricingService pricingService) {
        this.vehicleRepository = vehicleRepository;
        this.reservationRepository = reservationRepository;
        this.pricingService = pricingService;
    }

    public BookingResponse reserve(BookingRequest request) {
        validateDates(request.startDate(), request.endDate());

        Vehicle vehicle = findAvailableVehicle(
                request.category(),
                request.startDate(),
                request.endDate(),
                null);

        var total = pricingService.calculate(
                request.category(),
                request.startDate(),
                request.endDate(),
                request.dailyMileage(),
                request.licenseYears());

        Reservation reservation = new Reservation(
                UUID.randomUUID().toString(),
                request.customerName(),
                vehicle.id(),
                request.category(),
                request.startDate(),
                request.endDate(),
                total,
                true);

        return toResponse(reservationRepository.save(reservation));
    }

    public BookingResponse modify(String reservationId, BookingRequest request) {
        validateDates(request.startDate(), request.endDate());

        Reservation existing = getReservation(reservationId);

        if (!existing.active()) {
            throw new IllegalStateException("Reservation is already cancelled");
        }

        Vehicle vehicle = findAvailableVehicle(
                request.category(),
                request.startDate(),
                request.endDate(),
                reservationId);

        var total = pricingService.calculate(
                request.category(),
                request.startDate(),
                request.endDate(),
                request.dailyMileage(),
                request.licenseYears());

        Reservation updated = new Reservation(
                existing.id(),
                request.customerName(),
                vehicle.id(),
                request.category(),
                request.startDate(),
                request.endDate(),
                total,
                true);

        return toResponse(reservationRepository.save(updated));
    }

    public BookingResponse cancel(String reservationId) {
        Reservation existing = getReservation(reservationId);

        if (!existing.active()) {
            throw new IllegalStateException("Reservation is already cancelled");
        }

        return toResponse(reservationRepository.save(existing.cancelled()));
    }

    private Vehicle findAvailableVehicle(
            VehicleCategory category,
            LocalDate startDate,
            LocalDate endDate,
            String ignoredReservationId) {

        List<Reservation> activeReservations =
                reservationRepository.findAllActive();

        return vehicleRepository.findByCategory(category).stream()
                .filter(vehicle -> activeReservations.stream()
                        .filter(r -> !r.id().equals(ignoredReservationId))
                        .noneMatch(r -> r.vehicleId().equals(vehicle.id())
                                && overlaps(
                                r.startDate(), r.endDate(),
                                startDate, endDate)))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(
                        "No vehicle available for category " + category
                                + " for the requested dates"));
    }

    private boolean overlaps(
            LocalDate existingStart,
            LocalDate existingEnd,
            LocalDate requestedStart,
            LocalDate requestedEnd) {

        return existingStart.isBefore(requestedEnd)
                && existingEnd.isAfter(requestedStart);
    }

    private Reservation getReservation(String reservationId) {
        return reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Reservation not found: " + reservationId));
    }

    private void validateDates(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null
                || !endDate.isAfter(startDate)) {
            throw new IllegalArgumentException(
                    "endDate must be after startDate");
        }
    }

    private BookingResponse toResponse(Reservation reservation) {
        return new BookingResponse(
                reservation.id(),
                reservation.customerName(),
                reservation.vehicleId(),
                reservation.category(),
                reservation.startDate(),
                reservation.endDate(),
                reservation.totalAmount(),
                reservation.active() ? "ACTIVE" : "CANCELLED");
    }
}
