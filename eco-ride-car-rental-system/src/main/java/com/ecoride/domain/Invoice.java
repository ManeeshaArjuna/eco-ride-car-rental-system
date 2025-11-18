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

    // --- Colors for Theme A (EcoRide Green Theme) ---
    private static final String RESET        = "\u001B[0m";
    private static final String GREEN        = "\u001B[32m";
    private static final String BRIGHT_GREEN = "\u001B[92m";
    private static final String WHITE        = "\u001B[37m";
    private static final String GREY         = "\u001B[90m";
    private static final String NAVY_BLUE = "\u001B[34m";

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

    @Override
    public String toString() {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        StringBuilder sb = new StringBuilder();

        // ============================================================
        // HEADER
        // ============================================================
        sb.append("\n");
        sb.append(GREEN)
          .append("==============================================================\n")
          .append("                       🧾  ECO RIDE INVOICE                   \n")
          .append("==============================================================\n")
          .append(RESET);

        // ============================================================
        // BASIC INFO
        // ============================================================
        sb.append(NAVY_BLUE).append("Invoice ID   : ").append(RESET).append(invoiceId).append("\n");
        sb.append(NAVY_BLUE).append("Created At   : ").append(RESET).append(createdAt.format(fmt)).append("\n\n");

        // ============================================================
        // BOOKING + CUSTOMER + VEHICLE INFO SECTION
        // ============================================================
        sb.append(BRIGHT_GREEN).append("📘 BOOKING DETAILS").append(RESET).append("\n");
        sb.append(WHITE).append("Booking ID   : ").append(booking.getBookingId()).append("\n");
        sb.append("Customer     : ")
          .append(booking.getCustomer().getName())
          .append(" (").append(booking.getCustomer().getCustomerId()).append(")").append("\n");
        sb.append("Vehicle      : ")
          .append(booking.getVehicle().getVehicleId())
          .append(" - ").append(booking.getVehicle().getModel()).append("\n");
        sb.append("Category     : ")
          .append(booking.getVehicle().getCategory().getDisplayName()).append("\n");
        sb.append("Rental Days  : ").append(booking.rentalDays()).append("\n");
        sb.append("Total KM     : ")
          .append(booking.getTotalKm()).append(" km")
          .append("   ").append(GREY).append("(Free KM: ").append(booking.freeKmTotal()).append(")").append(RESET).append("\n");

        sb.append("\n");

        // ============================================================
        // PRICE BREAKDOWN SECTION
        // ============================================================
        sb.append(BRIGHT_GREEN).append("💰 PRICE BREAKDOWN").append(RESET).append("\n");

        sb.append(String.format(WHITE + "Base Price        : LKR %,.2f%n", basePrice));
        sb.append(String.format("Extra KM Charge   : LKR %,.2f%n", extraKmCharge));
        sb.append(String.format("Discount Applied  : -LKR %,.2f%n", discount));
        sb.append(String.format("Tax               : LKR %,.2f%n", tax));
        sb.append(String.format("Deposit Deducted  : -LKR %,.2f%n", depositDeducted));

        sb.append(GREY).append("--------------------------------------------------------------\n").append(RESET);

        // ============================================================
        // FINAL TOTAL SECTION
        // ============================================================
        sb.append(String.format(NAVY_BLUE + "TOTAL PAYABLE     : " + BRIGHT_GREEN + "LKR %,.2f%n" + RESET, finalPayable));

        sb.append(GREEN)
          .append("==============================================================\n")
          .append("        Thank you for choosing EcoRide! Drive Safe 🚗💚        \n")
          .append("==============================================================\n")
          .append(RESET);

        return sb.toString();
    }

    // Getter
    public BigDecimal getFinalPayable() { return finalPayable; }
}
