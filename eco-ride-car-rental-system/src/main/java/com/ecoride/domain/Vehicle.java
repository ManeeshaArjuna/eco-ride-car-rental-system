package com.ecoride.domain;

import java.util.Objects;

public abstract class Vehicle {
    protected String vehicleId;
    protected String model;
    protected Category category;
    protected AvailabilityStatus availabilityStatus;

    protected Vehicle(String vehicleId, String model, Category category, AvailabilityStatus status) {
        this.vehicleId = vehicleId;
        this.model = model;
        this.category = category;
        this.availabilityStatus = status;
    }

    public String getVehicleId() { return vehicleId; }
    public String getModel() { return model; }
    public Category getCategory() { return category; }
    public AvailabilityStatus getAvailabilityStatus() { return availabilityStatus; }

    public void setModel(String model) { this.model = model; }
    public void setCategory(Category category) { this.category = category; }
    public void setAvailabilityStatus(AvailabilityStatus status) { this.availabilityStatus = status; }

    public boolean checkAvailability() { return availabilityStatus == AvailabilityStatus.AVAILABLE; }
    public void markReserved() { this.availabilityStatus = AvailabilityStatus.RESERVED; }
    public void markAvailable() { this.availabilityStatus = AvailabilityStatus.AVAILABLE; }

    @Override public String toString() {
        return vehicleId + " | " + model + " | " + category + " | " + availabilityStatus;
    }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Vehicle v)) return false;
        return Objects.equals(vehicleId, v.vehicleId);
    }

    @Override public int hashCode() { return Objects.hash(vehicleId); }
}
