package com.ecoride;

import com.ecoride.cli.ConsoleUI;
import com.ecoride.repository.*;
import com.ecoride.service.*;

public class App {
    public static void main(String[] args) {
        // repositories
        VehicleRepository vehicleRepo = new InMemoryVehicleRepository();
        CustomerRepository customerRepo = new InMemoryCustomerRepository();
        BookingRepository bookingRepo = new InMemoryBookingRepository();

        // services
        BookingPolicy policy = new BookingPolicy();
        PricingService pricing = new PricingService();
        CarRentalSystem system = new CarRentalSystem(vehicleRepo, customerRepo, bookingRepo, policy, pricing);

        // seed vehicles
        system.seedVehicles();
        system.seedAdmins(); // default admin: admin / admin123

        // launch CLI
        new ConsoleUI(system).start();
    }
}
