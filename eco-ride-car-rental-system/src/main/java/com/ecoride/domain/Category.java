package com.ecoride.domain;

import java.math.BigDecimal;

public enum Category {
    COMPACT_PETROL("Compact Petrol", new BigDecimal("5000"), 100, new BigDecimal("50"), new BigDecimal("0.10")),
    HYBRID("Hybrid", new BigDecimal("7500"), 150, new BigDecimal("60"), new BigDecimal("0.12")),
    ELECTRIC("Electric", new BigDecimal("10000"), 200, new BigDecimal("40"), new BigDecimal("0.08")),
    LUXURY_SUV("Luxury SUV", new BigDecimal("15000"), 250, new BigDecimal("75"), new BigDecimal("0.15"));

    private final String displayName;
    private final BigDecimal dailyRentalFee;
    private final int freeKmPerDay;
    private final BigDecimal extraKmCharge;
    private final BigDecimal taxRate;

    Category(String displayName, BigDecimal fee, int freeKmPerDay, BigDecimal extraKmCharge, BigDecimal taxRate) {
        this.displayName = displayName;
        this.dailyRentalFee = fee;
        this.freeKmPerDay = freeKmPerDay;
        this.extraKmCharge = extraKmCharge;
        this.taxRate = taxRate;
    }

    public String getDisplayName() { return displayName; }
    public BigDecimal getDailyRentalFee() { return dailyRentalFee; }
    public int getFreeKmPerDay() { return freeKmPerDay; }
    public BigDecimal getExtraKmCharge() { return extraKmCharge; }
    public BigDecimal getTaxRate() { return taxRate; }
}
