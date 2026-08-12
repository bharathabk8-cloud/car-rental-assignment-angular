package com.assignment.carrental.repository;

import com.assignment.carrental.domain.Reservation;

import java.util.List;
import java.util.Optional;

public interface ReservationRepository {
    Reservation save(Reservation reservation);
    Optional<Reservation> findById(String id);
    List<Reservation> findAllActive();
}
