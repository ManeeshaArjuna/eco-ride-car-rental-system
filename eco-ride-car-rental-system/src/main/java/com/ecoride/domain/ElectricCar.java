package com.ecoride.domain;

public class ElectricCar extends Vehicle {
    private double batteryCapacity; // kWh
    private double chargingTime;    // hours

    public ElectricCar(String id, String model, double batteryCapacity, double chargingTime) {
        super(id, model, Category.ELECTRIC, AvailabilityStatus.AVAILABLE);
        this.batteryCapacity = batteryCapacity;
        this.chargingTime = chargingTime;
    }

    public double getBatteryCapacity() { return batteryCapacity; }
    public double getChargingTime() { return chargingTime; }
    public void setBatteryCapacity(double v) { this.batteryCapacity = v; }
    public void setChargingTime(double v) { this.chargingTime = v; }

    public void displayElectricDetails() {
        System.out.println("ElectricCar: " + getModel() + " battery=" + batteryCapacity + "kWh, charge time=" + chargingTime + "h");
    }
}
