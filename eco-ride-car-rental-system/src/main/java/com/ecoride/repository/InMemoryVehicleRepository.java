package com.ecoride.repository;

import com.ecoride.domain.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class InMemoryVehicleRepository implements VehicleRepository {
    private final Map<String, Vehicle> data = new ConcurrentHashMap<>();

    @Override public void save(Vehicle v) { data.put(v.getVehicleId(), v); }
    @Override public Optional<Vehicle> findById(String id) { return Optional.ofNullable(data.get(id)); }
    @Override public List<Vehicle> findAll() { return new ArrayList<>(data.values()); }
    @Override public void delete(String id) { data.remove(id); }

    @Override public List<Vehicle> findAvailableByCategory(Category c) {
        return data.values().stream()
                .filter(v -> v.getCategory() == c && v.getAvailabilityStatus() == AvailabilityStatus.AVAILABLE)
                .collect(Collectors.toList());
    }
}
