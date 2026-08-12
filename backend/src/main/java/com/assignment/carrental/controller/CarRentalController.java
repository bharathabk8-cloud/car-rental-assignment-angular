package com.assignment.carrental.controller;

import com.assignment.carrental.dto.BookingRequest;
import com.assignment.carrental.dto.BookingResponse;
import com.assignment.carrental.dto.PriceOption;
import com.assignment.carrental.service.PricingService;
import com.assignment.carrental.service.ReservationService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:4200")
public class CarRentalController {

    private final ReservationService reservationService;
    private final PricingService pricingService;

    public CarRentalController(
            ReservationService reservationService,
            PricingService pricingService) {
        this.reservationService = reservationService;
        this.pricingService = pricingService;
    }

    @GetMapping("/options")
    public List<PriceOption> getOptions(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate endDate,
            @RequestParam(defaultValue = "0") BigDecimal dailyMileage,
            @RequestParam(defaultValue = "0") int licenseYears) {

        if (!endDate.isAfter(startDate)) {
            throw new IllegalArgumentException(
                    "endDate must be after startDate");
        }

        if (dailyMileage.signum() < 0 || licenseYears < 0) {
            throw new IllegalArgumentException(
                    "dailyMileage and licenseYears cannot be negative");
        }

        return pricingService.getOptions(
                startDate, endDate, dailyMileage, licenseYears);
    }

    @PostMapping("/reservations")
    @ResponseStatus(HttpStatus.CREATED)
    public BookingResponse reserve(
            @Valid @RequestBody BookingRequest request) {
        return reservationService.reserve(request);
    }

    @PutMapping("/reservations/{reservationId}")
    public BookingResponse modify(
            @PathVariable String reservationId,
            @Valid @RequestBody BookingRequest request) {
        return reservationService.modify(reservationId, request);
    }

    @DeleteMapping("/reservations/{reservationId}")
    public BookingResponse cancel(@PathVariable String reservationId) {
        return reservationService.cancel(reservationId);
    }
}
