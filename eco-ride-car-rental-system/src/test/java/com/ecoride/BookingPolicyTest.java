package com.ecoride;

import com.ecoride.domain.*;
import com.ecoride.service.*;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class BookingPolicyTest {

    @Test
    public void testLeadTimeReject() {
        BookingPolicy p = new BookingPolicy();
        Vehicle v = new Vehicle("C-1","Any", Category.COMPACT_PETROL, AvailabilityStatus.AVAILABLE) {};
        assertThrows(IllegalArgumentException.class, () -> p.ensureCanBook(v, LocalDate.now().plusDays(2)));
    }

    @Test
    public void testLeadTimeAccept() {
        BookingPolicy p = new BookingPolicy();
        Vehicle v = new Vehicle("C-1","Any", Category.COMPACT_PETROL, AvailabilityStatus.AVAILABLE) {};
        assertDoesNotThrow(() -> p.ensureCanBook(v, LocalDate.now().plusDays(3)));
    }
}
