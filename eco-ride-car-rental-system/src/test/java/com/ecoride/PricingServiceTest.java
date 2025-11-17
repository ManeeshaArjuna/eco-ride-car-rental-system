package com.ecoride;

import com.ecoride.domain.*;
import com.ecoride.service.*;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class PricingServiceTest {

    @Test
    public void testCompact8Days850km() {
        // Arrange
        Vehicle v = new Vehicle("C-100","Toyota Corolla", Category.COMPACT_PETROL, AvailabilityStatus.AVAILABLE) {};
        Customer c = new LocalCustomer("NIC1", "Alice", "0771234567", "a@ex.com");
        Booking b = new Booking("R-TEST", LocalDateTime.now(), LocalDate.now().plusDays(4),
                LocalDate.now().plusDays(11), 850, BookingPolicy.DEPOSIT, BookingStatus.ACTIVE, c, v);

        // Act
        PricingService pricing = new PricingService();

        // Assert
        assertEquals(new BigDecimal("40000"), pricing.basePrice(b));
        assertEquals(new BigDecimal("2500"), pricing.extraKmCharge(b)); // free 800, extra 50 * 50
        assertEquals(new BigDecimal("4000.00"), pricing.discount(b).setScale(2));
        assertEquals(new BigDecimal("3850.00"), pricing.tax(b).setScale(2)); // (40000-4000+2500)*10%
        assertEquals(new BigDecimal("37350.00"), pricing.finalPayable(b).setScale(2)); // total-tax-deposit
    }
}
