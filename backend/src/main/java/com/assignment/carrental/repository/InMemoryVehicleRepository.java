package com.assignment.carrental.repository;

import com.assignment.carrental.domain.Vehicle;
import com.assignment.carrental.domain.VehicleCategory;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class InMemoryVehicleRepository implements VehicleRepository {

    private final Map<String, Vehicle> vehicles = Map.of(
            "SEDAN-001", new Vehicle("SEDAN-001", VehicleCategory.SEDAN),
            "SEDAN-002", new Vehicle("SEDAN-002", VehicleCategory.SEDAN),
            "SUV-001", new Vehicle("SUV-001", VehicleCategory.SUV),
            "SUV-002", new Vehicle("SUV-002", VehicleCategory.SUV),
            "VAN-001", new Vehicle("VAN-001", VehicleCategory.VAN),
            "VAN-002", new Vehicle("VAN-002", VehicleCategory.VAN),
            "PICKUP-001", new Vehicle("PICKUP-001", VehicleCategory.PICKUP_TRUCK),
            "PICKUP-002", new Vehicle("PICKUP-002", VehicleCategory.PICKUP_TRUCK)
    );

    @Override
    public List<Vehicle> findByCategory(VehicleCategory category) {
        return vehicles.values().stream()
                .filter(v -> v.category() == category)
                .toList();
    }

    @Override
    public Optional<Vehicle> findById(String id) {
        return Optional.ofNullable(vehicles.get(id));
    }
}
