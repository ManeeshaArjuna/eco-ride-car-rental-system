package com.ecoride.service;

import com.ecoride.domain.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class BookingPolicy {
    public static final BigDecimal DEPOSIT = new BigDecimal("5000");

    public void ensureCanBook(Vehicle v, LocalDate startDate) {
        if (v.getAvailabilityStatus() != AvailabilityStatus.AVAILABLE) {
            throw new IllegalStateException("Vehicle is not available.");
        }
        LocalDate today = LocalDate.now();
        long daysAhead = ChronoUnit.DAYS.between(today, startDate);
        if (daysAhead < 3) {
            throw new IllegalArgumentException("Booking must be scheduled at least 3 days in advance.");
        }
    }

    public void ensureCanAmendOrCancel(Booking booking) {
        long daysSince = ChronoUnit.DAYS.between(booking.getBookingDate(), LocalDateTime.now());
        if (daysSince > 2) {
            throw new IllegalStateException("Cannot update/cancel after 2 days from reservation.");
        }
    }
}
