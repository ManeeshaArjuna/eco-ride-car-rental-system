// Remove the package declaration to match the expected package

import com.ecoride.repository.*;
import com.ecoride.service.*;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AdminAuthTest {

    @Test
    public void testAdminAuthentication() {
        VehicleRepository vRepo = new InMemoryVehicleRepository();
        CustomerRepository cRepo = new InMemoryCustomerRepository();
        BookingRepository bRepo = new InMemoryBookingRepository();

        CarRentalSystem system = new CarRentalSystem(vRepo, cRepo, bRepo, new BookingPolicy(), new PricingService());
        system.addAdmin("admin", "admin123");

        assertTrue(system.authenticateAdmin("admin", "admin123"));
        assertFalse(system.authenticateAdmin("admin", "wrong"));
        assertFalse(system.authenticateAdmin("nope", "admin123"));
    }
}
