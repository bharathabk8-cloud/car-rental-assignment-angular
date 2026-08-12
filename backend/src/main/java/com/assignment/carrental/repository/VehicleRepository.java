package com.assignment.carrental.repository;

import com.assignment.carrental.domain.Vehicle;
import com.assignment.carrental.domain.VehicleCategory;

import java.util.List;
import java.util.Optional;

public interface VehicleRepository {
    List<Vehicle> findByCategory(VehicleCategory category);
    Optional<Vehicle> findById(String id);
}
