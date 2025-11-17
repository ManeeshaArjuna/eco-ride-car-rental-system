package com.ecoride.domain;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class Booking {
    private String bookingId;
    private LocalDateTime bookingDate;
    private LocalDate startDate;
    private LocalDate endDate;
    private int totalKm;
    private BigDecimal deposit;
    private BookingStatus status;

    private Customer customer;
    private Vehicle vehicle;

    public Booking(String bookingId, LocalDateTime bookingDate, LocalDate startDate, LocalDate endDate, int totalKm,
                   BigDecimal deposit, BookingStatus status, Customer customer, Vehicle vehicle) {
        this.bookingId = bookingId;
        this.bookingDate = bookingDate;
        this.startDate = startDate;
        this.endDate = endDate;
        this.totalKm = totalKm;
        this.deposit = deposit;
        this.status = status;
        this.customer = customer;
        this.vehicle = vehicle;
    }

    public String getBookingId() { return bookingId; }
    public LocalDateTime getBookingDate() { return bookingDate; }
    public LocalDate getStartDate() { return startDate; }
    public LocalDate getEndDate() { return endDate; }
    public int getTotalKm() { return totalKm; }
    public BigDecimal getDeposit() { return deposit; }
    public BookingStatus getStatus() { return status; }
    public Customer getCustomer() { return customer; }
    public Vehicle getVehicle() { return vehicle; }

    public void setTotalKm(int km) { this.totalKm = km; }
    public void setStartDate(LocalDate d) { this.startDate = d; }
    public void setEndDate(LocalDate d) { this.endDate = d; }

    public int rentalDays() {
        long d = ChronoUnit.DAYS.between(startDate, endDate) + 1; // inclusive
        return (int)Math.max(1, d);
    }

    public BigDecimal calculateBasePrice() {
        return vehicle.getCategory().getDailyRentalFee().multiply(BigDecimal.valueOf(rentalDays()));
    }

    public int freeKmTotal() {
        return vehicle.getCategory().getFreeKmPerDay() * rentalDays();
    }

    public BigDecimal calculateExtraKmCharge() {
        int extra = Math.max(0, totalKm - freeKmTotal());
        return vehicle.getCategory().getExtraKmCharge().multiply(BigDecimal.valueOf(extra));
    }

    public BigDecimal calculateDiscount() {
        if (rentalDays() >= 7) {
            return calculateBasePrice().multiply(new BigDecimal("0.10"));
        }
        return BigDecimal.ZERO;
    }

    public BigDecimal calculateTax() {
        BigDecimal taxable = calculateBasePrice().subtract(calculateDiscount()).add(calculateExtraKmCharge());
        return taxable.multiply(vehicle.getCategory().getTaxRate());
    }

    public BigDecimal calculateFinalAmount() {
        BigDecimal subtotal = calculateBasePrice().subtract(calculateDiscount()).add(calculateExtraKmCharge()).add(calculateTax());
        BigDecimal finalPayable = subtotal.subtract(deposit);
        return finalPayable.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO : finalPayable;
    }

    public void confirmBooking() {
        this.status = BookingStatus.ACTIVE;
    }

    public void complete() {
        this.status = BookingStatus.COMPLETED;
    }

    public void cancelBooking() {
        this.status = BookingStatus.CANCELLED;
    }

    @Override public String toString() {
        return bookingId + " | " + startDate + " to " + endDate + " | " + vehicle.getVehicleId() + " | " + customer.getCustomerId() + " | " + status;
    }
}
