package com.ecoride.domain;

public class HybridCar extends Vehicle {
    private double batteryCapacity; // kWh
    private double fuelEfficiency;  // km/l

    public HybridCar(String id, String model, double batteryCapacity, double fuelEfficiency) {
        super(id, model, Category.HYBRID, AvailabilityStatus.AVAILABLE);
        this.batteryCapacity = batteryCapacity;
        this.fuelEfficiency = fuelEfficiency;
    }

    public double getBatteryCapacity() { return batteryCapacity; }
    public double getFuelEfficiency() { return fuelEfficiency; }

    public void setBatteryCapacity(double v) { this.batteryCapacity = v; }
    public void setFuelEfficiency(double v) { this.fuelEfficiency = v; }

    public void displayHybridDetails() {
        System.out.println("HybridCar: " + getModel() + " battery=" + batteryCapacity + "kWh, efficiency=" + fuelEfficiency + " km/l");
    }
}
