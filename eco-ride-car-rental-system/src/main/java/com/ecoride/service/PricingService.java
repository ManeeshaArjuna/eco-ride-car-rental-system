package com.ecoride.service;

import com.ecoride.domain.*;

import java.math.BigDecimal;

public class PricingService {

    public BigDecimal basePrice(Booking b) { return b.calculateBasePrice(); }

    public BigDecimal extraKmCharge(Booking b) { return b.calculateExtraKmCharge(); }

    public BigDecimal discount(Booking b) { return b.calculateDiscount(); }

    public BigDecimal tax(Booking b) { return b.calculateTax(); }

    public BigDecimal finalPayable(Booking b) { return b.calculateFinalAmount(); }
}
