package com.ecoride.domain;

public class CompactPetrolCar extends Vehicle {
    private double engineCapacity; // liters
    private String transmission;   // AUTO / MANUAL

    public CompactPetrolCar(String id, String model, double engineCapacity, String transmission) {
        super(id, model, Category.COMPACT_PETROL, AvailabilityStatus.AVAILABLE);
        this.engineCapacity = engineCapacity;
        this.transmission = transmission;
    }

    public double getEngineCapacity() { return engineCapacity; }
    public void setEngineCapacity(double v) { this.engineCapacity = v; }

    public String getTransmission() { return transmission; }
    public void setTransmission(String t) { this.transmission = t; }

    public void displayCompactDetails() {
        System.out.println("Compact Petrol: " + getModel() + " " + engineCapacity + "L " + transmission);
    }
}
