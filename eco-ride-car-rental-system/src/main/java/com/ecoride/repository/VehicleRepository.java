package com.ecoride.repository;

import com.ecoride.domain.*;

import java.util.*;

public interface VehicleRepository {
    void save(Vehicle v);
    Optional<Vehicle> findById(String id);
    List<Vehicle> findAll();
    void delete(String id);
    List<Vehicle> findAvailableByCategory(Category c);
}
