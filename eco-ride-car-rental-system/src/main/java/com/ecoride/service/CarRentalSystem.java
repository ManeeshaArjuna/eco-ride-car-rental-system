package com.ecoride.service;

import com.ecoride.domain.*;
import com.ecoride.repository.*;
import com.ecoride.util.IdGenerator;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class CarRentalSystem {

    private final VehicleRepository vehicleRepo;
    private final CustomerRepository customerRepo;
    private final BookingRepository bookingRepo;
    private final BookingPolicy policy;
    /** Simple in-memory admin credential store: adminId -> password */
    private final Map<String, String> adminUsers = new HashMap<>();

    public CarRentalSystem(VehicleRepository vehicleRepo, CustomerRepository customerRepo,
                           BookingRepository bookingRepo, BookingPolicy policy, PricingService pricing) {
        this.vehicleRepo = vehicleRepo;
        this.customerRepo = customerRepo;
        this.bookingRepo = bookingRepo;
        this.policy = policy;
    }

    // ---- Admin auth ----
     public void addAdmin(String adminId, String password) {
         adminUsers.put(adminId, password);
     }
 
     public boolean authenticateAdmin(String adminId, String password) {
         return adminUsers.containsKey(adminId) && Objects.equals(adminUsers.get(adminId), password);
     }

     /** Seed a default admin account for the console demo. */
     public void seedAdmins() {
         addAdmin("admin", "admin123");
     }

    // ---- Vehicles ----
    public void addVehicle(Vehicle v) { vehicleRepo.save(v); }
    public void updateVehicle(Vehicle v) { vehicleRepo.save(v); }
    public void removeVehicle(String id) { vehicleRepo.delete(id); }
    public List<Vehicle> listVehicles() { return vehicleRepo.findAll(); }
    public void changeAvailability(String id, AvailabilityStatus status) {
        Vehicle v = vehicleRepo.findById(id).orElseThrow(() -> new IllegalArgumentException("Not found"));
        v.setAvailabilityStatus(status);
        vehicleRepo.save(v);
    }
    public List<Vehicle> listAvailableByCategory(Category c) { return vehicleRepo.findAvailableByCategory(c); }

    // ---- Customers ----
    public void addCustomer(Customer c) { customerRepo.save(c); }
    public Optional<Customer> findCustomer(String id) { return customerRepo.findById(id); }
    public List<Customer> searchCustomersByName(String name) { return customerRepo.findByNameContains(name); }

    // ---- Booking ----
    public Booking bookByCategory(String customerId, Category category, LocalDate start, int days, int totalKm) {
        List<Vehicle> avail = listAvailableByCategory(category);
        if (avail.isEmpty()) throw new IllegalStateException("No available vehicle in " + category);
        return bookSpecific(customerId, avail.get(0).getVehicleId(), start, days, totalKm);
    }

    public Booking bookSpecific(String customerId, String vehicleId, LocalDate start, int days, int totalKm) {
        Customer c = customerRepo.findById(customerId).orElseThrow(() -> new IllegalArgumentException("Customer not found"));
        Vehicle v = vehicleRepo.findById(vehicleId).orElseThrow(() -> new IllegalArgumentException("Vehicle not found"));
        policy.ensureCanBook(v, start);

        String bookingId = "R-" + IdGenerator.shortId();
        Booking b = new Booking(bookingId, LocalDateTime.now(), start, start.plusDays(days-1), totalKm,
                BookingPolicy.DEPOSIT, BookingStatus.ACTIVE, c, v);
        v.markReserved();
        vehicleRepo.save(v);
        bookingRepo.save(b);
        return b;
    }

    public Booking updateBooking(String bookingId, LocalDate newStart, Integer newDays, Integer newTotalKm) {
        Booking b = bookingRepo.findById(bookingId).orElseThrow(() -> new IllegalArgumentException("Booking not found"));
        policy.ensureCanAmendOrCancel(b);
        if (newStart != null) {

            // Allow update if the only conflict is the SAME booking
            boolean conflict =
                bookingRepo.findAll().stream()
                    .filter(x -> !x.getBookingId().equals(bookingId)) // exclude this booking
                    .anyMatch(x -> x.getVehicle().getVehicleId().equals(b.getVehicle().getVehicleId()) &&
                                x.getStatus() == BookingStatus.ACTIVE &&
                                !newStart.isAfter(x.getEndDate()) &&
                                !newStart.isBefore(x.getStartDate()));

            if (conflict) {
                throw new IllegalArgumentException("Vehicle is not available on the selected new start date.");
            }

            // Now safe to update
            b.setStartDate(newStart);
            int days = newDays != null ? newDays : b.rentalDays();
            b.setEndDate(newStart.plusDays(days - 1));
        }
        if (newDays != null && newStart == null) {
            b.setEndDate(b.getStartDate().plusDays(newDays-1));
        }
        if (newTotalKm != null) b.setTotalKm(newTotalKm);
        bookingRepo.save(b);
        return b;
    }

    public void cancelBooking(String bookingId) {
        Booking b = bookingRepo.findById(bookingId).orElseThrow(() -> new IllegalArgumentException("Booking not found"));
        policy.ensureCanAmendOrCancel(b);
        b.cancelBooking();
        Vehicle v = b.getVehicle();
        v.markAvailable();
        vehicleRepo.save(v);
        bookingRepo.save(b);
    }

    public Invoice completeAndInvoice(String bookingId) {
        var opt = bookingRepo.findById(bookingId);

        if (opt.isEmpty()) {
            throw new IllegalArgumentException("Booking not found: " + bookingId);
        }

        Booking b = opt.get();

        // ❌ Cannot complete cancelled bookings
        if (b.getStatus() == BookingStatus.CANCELLED) {
            throw new IllegalStateException("Cannot complete a cancelled booking.");
        }

        // ❌ Cannot complete already completed bookings
        if (b.getStatus() == BookingStatus.COMPLETED) {
            throw new IllegalStateException("Booking already completed.");
        }

        // Proceed with normal completion
        b.complete();   // sets status to COMPLETED
        return new Invoice(b);
    }

    public Optional<Booking> findBookingById(String id) { return bookingRepo.findById(id); }

    public List<Booking> searchBookingsByNameOrId(String q) {
        String query = q.toLowerCase(Locale.ROOT);
        return bookingRepo.findAll().stream().filter(b ->
                b.getBookingId().toLowerCase(Locale.ROOT).contains(query) ||
                b.getCustomer().getName().toLowerCase(Locale.ROOT).contains(query)).collect(Collectors.toList());
    }

    public List<Booking> viewBookingsByDate(LocalDate d) { return bookingRepo.findByDate(d); }

    // ---- Seeding ----

    /** Generate next vehicle ID like C-001, C-002 … scanning existing vehicles. */
    public String generateVehicleId() {
        int max = 0;
        for (var v : vehicleRepo.findAll()) {
            String id = v.getVehicleId();
            if (id != null && id.startsWith("C-")) {
                try {
                    int n = Integer.parseInt(id.substring(2));
                    if (n > max) max = n;
                } catch (NumberFormatException ignored) { /* skip non-numeric tails */ }
            }
        }
        return String.format("C-%03d", max + 1);
    }

    public void seedVehicles() {
        addVehicle(new HybridCar("C-001", "Toyota Aqua", 6.5, 25));
        addVehicle(new ElectricCar("C-002", "Nissan Leaf", 40, 7.0));
        addVehicle(new LuxurySUVCar("C-003", "BMW X5", "Leather, Sunroof", true));
        addVehicle(new ElectricCar("C-004", "BYD Atto 3", 60, 8.0));
        addVehicle(new CompactPetrolCar("C-005", "Toyota Corolla", 1.5, "AUTO"));
    }
}
