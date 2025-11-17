package com.ecoride.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class Invoice {
    private String invoiceId;
    private LocalDateTime createdAt;
    private Booking booking;

    private BigDecimal basePrice;
    private BigDecimal extraKmCharge;
    private BigDecimal discount;
    private BigDecimal tax;
    private BigDecimal depositDeducted;
    private BigDecimal finalPayable;

    public Invoice(Booking booking) {
        this.invoiceId = "INV-" + UUID.randomUUID().toString().substring(0,8);
        this.createdAt = LocalDateTime.now();
        this.booking = booking;

        this.basePrice = booking.calculateBasePrice();
        this.extraKmCharge = booking.calculateExtraKmCharge();
        this.discount = booking.calculateDiscount();
        this.tax = booking.calculateTax();
        this.depositDeducted = booking.getDeposit();
        this.finalPayable = booking.calculateFinalAmount();
    }

    @Override public String toString() {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        StringBuilder sb = new StringBuilder();
        sb.append("===== EcoRide Invoice =====\n");
        sb.append("Invoice ID: ").append(invoiceId).append("\n");
        sb.append("Created At: ").append(createdAt.format(fmt)).append("\n\n");
        sb.append("Booking ID: ").append(booking.getBookingId()).append("\n");
        sb.append("Customer: ").append(booking.getCustomer().getName()).append(" (").append(booking.getCustomer().getCustomerId()).append(")\n");
        sb.append("Vehicle: ").append(booking.getVehicle().getModel()).append(" (").append(booking.getVehicle().getVehicleId()).append(")\n");
        sb.append("Category: ").append(booking.getVehicle().getCategory().getDisplayName()).append("\n");
        sb.append("Rental Days: ").append(booking.rentalDays()).append("\n");
        sb.append("Mileage Used: ").append(booking.getTotalKm()).append(" km (free ").append(booking.freeKmTotal()).append(")\n\n");

        sb.append("Base Price: LKR ").append(basePrice).append("\n");
        sb.append("Extra KM Charge: LKR ").append(extraKmCharge).append("\n");
        sb.append("Discount: -LKR ").append(discount).append("\n");
        sb.append("Tax: LKR ").append(tax).append("\n");
        sb.append("Deposit Deducted: -LKR ").append(depositDeducted).append("\n");
        sb.append("Final Payable: LKR ").append(finalPayable).append("\n");
        sb.append("===========================\n");
        return sb.toString();
    }

    // Getters (useful if needed later)
    public BigDecimal getFinalPayable() { return finalPayable; }
}
