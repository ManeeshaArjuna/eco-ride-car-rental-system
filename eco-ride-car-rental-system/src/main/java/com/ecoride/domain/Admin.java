package com.ecoride.domain;

import com.ecoride.service.CarRentalSystem;

public class Admin {
    private String adminId;
    private String name;

    public Admin(String adminId, String name) {
        this.adminId = adminId;
        this.name = name;
    }

    public String getAdminId() { return adminId; }
    public String getName() { return name; }

    // Thin wrappers that delegate to the system (useful to reflect the UML actor)
    public void addVehicle(CarRentalSystem system, Vehicle v) { system.addVehicle(v); }
    public void updateVehicle(CarRentalSystem system, Vehicle v) { system.updateVehicle(v); }
    public void removeVehicle(CarRentalSystem system, String id) { system.removeVehicle(id); }
    public void changeAvailability(CarRentalSystem system, String id, AvailabilityStatus st) { system.changeAvailability(id, st); }
}
