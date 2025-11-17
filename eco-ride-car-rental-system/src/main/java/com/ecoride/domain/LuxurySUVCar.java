package com.ecoride.domain;

public class LuxurySUVCar extends Vehicle {
    private String luxuryFeatures;
    private boolean driverIncluded;

    public LuxurySUVCar(String id, String model, String luxuryFeatures, boolean driverIncluded) {
        super(id, model, Category.LUXURY_SUV, AvailabilityStatus.AVAILABLE);
        this.luxuryFeatures = luxuryFeatures;
        this.driverIncluded = driverIncluded;
    }

    public String getLuxuryFeatures() { return luxuryFeatures; }
    public boolean isDriverIncluded() { return driverIncluded; }
    public void setLuxuryFeatures(String f) { this.luxuryFeatures = f; }
    public void setDriverIncluded(boolean d) { this.driverIncluded = d; }

    public void displayLuxuryDetails() {
        System.out.println("LuxurySUV: " + getModel() + " features=" + luxuryFeatures + " driverIncluded=" + driverIncluded);
    }
}
