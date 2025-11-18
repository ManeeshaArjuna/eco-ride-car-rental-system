package com.ecoride.cli;

import com.ecoride.domain.*;
import com.ecoride.service.CarRentalSystem;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.Optional;

public class ConsoleUI {

    private final CarRentalSystem system;

    public ConsoleUI(CarRentalSystem system) {
        this.system = system;
    }

    // ============================================================
    // GLOBAL SAFE INPUT WITH BACK (#)
    // ============================================================
    private String readInput(Scanner sc) {
        String input = sc.nextLine().trim();

        if (input.equals("#")) {
            System.out.println("↩ Returning to main menu...");
            System.out.println();
            throw new RuntimeException("BACK_TO_HOME");
        }
        return input;
    }

    // ============================================================
    // STRICT BOOKING ID VALIDATOR  (R-xxxxxxxx)
    // ============================================================
    private String askValidBookingId(Scanner sc) {
        while (true) {
            System.out.print("Booking ID (e.g., R-ca4a1d44): ");
            String id = readInput(sc);

            // Validate exact pattern: R- followed by 8 hex lowercase chars
            if (!id.matches("R-[0-9a-f]{8}")) {
                System.out.println("❌ Invalid Booking ID format! Expected format: R-xxxxxxxx");
                continue;
            }

            Optional<Booking> booking = system.findBookingById(id);

            if (booking.isEmpty()) {
                System.out.println("❌ No booking found with ID: " + id);
                continue;
            }

            return id;
        }
    }

    // ============================================================
    // VEHICLE ID VALIDATOR (C-001)
    // ============================================================
    private String askValidVehicleId(Scanner sc) {
        while (true) {
            System.out.print("Vehicle ID (e.g., C-001): ");
            String vid = readInput(sc);

            if (!vid.matches("C-\\d{3}")) {
                System.out.println("❌ Invalid ID format! Expected: C-001");
                continue;
            }

            boolean exists = system.listVehicles().stream()
                    .anyMatch(v -> v.getVehicleId().equals(vid));

            if (!exists) {
                System.out.println("❌ Vehicle not found: " + vid);
                continue;
            }

            return vid;
        }
    }

    // ============================================================
    // MAIN LOOP
    // ============================================================
    public void start() {
        System.out.println();
        System.out.println("===============================================");
        System.out.println("|=== Welcome to EcoRide Car Rental System ===|");
        System.out.println("===============================================");
        System.out.println("Developed by Maneesha Arjuna - ESOFT UNI KANDY");
        System.out.println("-----------------------------------------------");

        Scanner sc = new Scanner(System.in);

        while (true) {
            printMenu();

            try {
                String choice = readInput(sc);

                switch (choice) {
                    case "1" -> registerCustomer(sc);
                    case "2" -> { if (requireAdmin(sc)) vehicleManagement(sc); }
                    case "3" -> makeBooking(sc);
                    case "4" -> updateBooking(sc);
                    case "5" -> cancelBooking(sc);
                    case "6" -> searchBookings(sc);
                    case "7" -> viewBookingsByDate(sc);
                    case "8" -> completeAndInvoice(sc);
                    case "9" -> listVehicles();
                    case "0" -> { System.out.println("Goodbye!"); return; }
                    default -> System.out.println("Invalid option.");
                }

            } catch (RuntimeException ex) {
                if ("BACK_TO_HOME".equals(ex.getMessage()))
                    continue;

                System.out.println("Error: " + ex.getMessage());
            }
        }
    }

    // ============================================================
    // ADMIN AUTH
    // ============================================================
    private boolean requireAdmin(Scanner sc) {
        System.out.println();
        System.out.println("---------------------------------------------");
        System.out.println("|       Admin authentication required       |");
        System.out.println("---------------------------------------------");
        System.out.println();

        System.out.print("Admin ID: ");
        String id = readInput(sc);

        System.out.print("Password: ");
        String pwd = readInput(sc);

        boolean ok = system.authenticateAdmin(id, pwd);

        if (!ok) System.out.println("Access denied: invalid credentials.");

        return ok;
    }

    private void printMenu() {
        System.out.println();
        System.out.println("---------------------------------------------");
        System.out.println("|=== EcoRide Car Rental System (Console) ===|");
        System.out.println("---------------------------------------------");
        System.out.println();
        System.out.println("1) Register customer");
        System.out.println("2) Vehicle management");
        System.out.println("3) Book car");
        System.out.println("4) Update booking (<= 2 days)");
        System.out.println("5) Cancel booking (<= 2 days)");
        System.out.println("6) Search bookings");
        System.out.println("7) View bookings by start date");
        System.out.println("8) Complete booking & generate invoice");
        System.out.println("9) List vehicles");
        System.out.println("0) Exit");
        System.out.println("(Tip: Enter '#' anytime to return to main menu)");
        System.out.println();
        System.out.print("Choose: ");
    }

    // ============================================================
    // CUSTOMER REGISTRATION
    // ============================================================
    private void registerCustomer(Scanner sc) {
        System.out.println();
        System.out.println("=============================================");
        System.out.println("REGISTER CUSTOMER");
        System.out.println("=============================================");

        System.out.println("Customer type: 1) Local  2) Foreign");
        String type;

        while (true) {
            System.out.print("Choose (1 or 2): ");
            type = readInput(sc);

            if (type.equals("1") || type.equals("2"))
                break;

            System.out.println("❌ Invalid choice! Enter 1 or 2.");
        }

        System.out.print("Name: ");
        String name = readInput(sc);
        System.out.print("Contact number: ");
        String contact = readInput(sc);
        System.out.print("Email: ");
        String email = readInput(sc);

        if (type.equals("1")) {
            System.out.print("NIC: ");
            String nic = readInput(sc);
            system.addCustomer(new LocalCustomer(nic, name, contact, email));
        } else {
            System.out.print("Passport: ");
            String pass = readInput(sc);
            System.out.print("Nationality: ");
            String nat = readInput(sc);
            system.addCustomer(new ForeignCustomer(pass, nat, name, contact, email));
        }

        System.out.println("✔ Customer registered successfully!");
    }

    // ============================================================
    // VEHICLE MANAGEMENT
    // ============================================================
    private void vehicleManagement(Scanner sc) {
        while (true) {
            System.out.println();
            System.out.println("=============================================");
            System.out.println("VEHICLE MANAGEMENT");
            System.out.println("=============================================");
            System.out.println("a) Add   b) Update status   c) Remove   d) Back");
            System.out.print("Choose: ");

            String c = readInput(sc).toLowerCase();

            switch (c) {
                case "a" -> { handleAddVehicle(sc); return; }
                case "b" -> {
                    String id = askValidVehicleId(sc);
                    AvailabilityStatus st = askAvailabilityStatus(sc);
                    system.changeAvailability(id, st);
                    System.out.println("✔ Vehicle status updated.");
                    return;
                }
                case "c" -> {
                    String id = askValidVehicleId(sc);
                    system.removeVehicle(id);
                    System.out.println("✔ Vehicle removed.");
                    return;
                }
                case "d" -> { return; }
                default -> System.out.println("❌ Invalid choice!");
            }
        }
    }

    // ============================================================
    // ADD VEHICLE
    // ============================================================
    private void handleAddVehicle(Scanner sc) {
        System.out.println();
        System.out.println("Vehicle Type: 1) HYBRID 2) ELECTRIC 3) LUXURY_SUV 4) COMPACT_PETROL");

        String t;

        while (true) {
            System.out.print("Choose (1-4): ");
            t = readInput(sc);
            if (t.matches("[1-4]")) break;
            System.out.println("❌ Invalid type!");
        }

        String id = system.generateVehicleId();
        System.out.println("Assigned Vehicle ID: " + id);

        System.out.print("Model: ");
        String model = readInput(sc);

        try {
            switch (t) {
                case "1" -> {
                    System.out.print("Battery kWh: ");
                    double b = Double.parseDouble(readInput(sc));
                    System.out.print("Efficiency km/l: ");
                    double e = Double.parseDouble(readInput(sc));
                    system.addVehicle(new HybridCar(id, model, b, e));
                }
                case "2" -> {
                    System.out.print("Battery kWh: ");
                    double b = Double.parseDouble(readInput(sc));
                    System.out.print("Charge time (hours): ");
                    double h = Double.parseDouble(readInput(sc));
                    system.addVehicle(new ElectricCar(id, model, b, h));
                }
                case "3" -> {
                    System.out.print("Features: ");
                    String f = readInput(sc);
                    System.out.print("Driver included (true/false): ");
                    boolean d = Boolean.parseBoolean(readInput(sc));
                    system.addVehicle(new LuxurySUVCar(id, model, f, d));
                }
                case "4" -> {
                    System.out.print("Engine capacity (L): ");
                    double cap = Double.parseDouble(readInput(sc));
                    System.out.print("Transmission (AUTO/MANUAL): ");
                    String tr = readInput(sc);
                    system.addVehicle(new CompactPetrolCar(id, model, cap, tr));
                }
            }

            System.out.println("✔ Vehicle added successfully!");

        } catch (Exception ex) {
            System.out.println("❌ Invalid input — vehicle not added.");
        }
    }

    // ============================================================
    // CATEGORY SELECTOR
    // ============================================================
    private Category askCategory(Scanner sc) {
        System.out.println();
        System.out.println("Choose category:");
        System.out.println("1) COMPACT_PETROL");
        System.out.println("2) HYBRID");
        System.out.println("3) ELECTRIC");
        System.out.println("4) LUXURY_SUV");

        String s = readInput(sc);

        return switch (s) {
            case "1" -> Category.COMPACT_PETROL;
            case "2" -> Category.HYBRID;
            case "3" -> Category.ELECTRIC;
            case "4" -> Category.LUXURY_SUV;
            default -> throw new IllegalArgumentException("Invalid category.");
        };
    }

    // ============================================================
    // CUSTOMER LOOKUP
    // ============================================================
    private String askExistingCustomerId(Scanner sc) {
        while (true) {
            System.out.print("Customer NIC/Passport: ");
            String cid = readInput(sc);

            if (cid.isBlank()) {
                System.out.println("❌ NIC / Passport cannot be blank.");
                continue;
            }

            var c = system.findCustomer(cid);

            if (c.isPresent()) {
                System.out.println("✔ Found: " + c.get().getName());
                return cid;
            }

            System.out.println("❌ Not found.");
            System.out.println("(R)egister  |  (T)ry again  |  Other: Cancel");

            String opt = readInput(sc).toLowerCase();

            if (opt.equals("r")) registerCustomer(sc);
            else if (opt.equals("t")) continue;
            else return null;
        }
    }

    // ============================================================
    // AVAILABILITY STATUS
    // ============================================================
    private AvailabilityStatus askAvailabilityStatus(Scanner sc) {
        while (true) {
            System.out.println("1) AVAILABLE");
            System.out.println("2) RESERVED");
            System.out.println("3) UNDER_MAINTENANCE");
            System.out.print("Choose: ");

            String s = readInput(sc);

            switch (s) {
                case "1":
                    return AvailabilityStatus.AVAILABLE;
                case "2":
                    return AvailabilityStatus.RESERVED;
                case "3":
                    return AvailabilityStatus.UNDER_MAINTENANCE;
                default:
                    // fall through to print invalid choice below
            }
            System.out.println("❌ Invalid choice.");
        }
    }

    // ============================================================
    // FRIENDLY DATE SELECTOR
    // ============================================================
    private LocalDate askStartDate(Scanner sc) {
        LocalDate today = LocalDate.now();
        LocalDate d3 = today.plusDays(3);
        LocalDate d4 = today.plusDays(4);
        LocalDate d5 = today.plusDays(5);

        System.out.println("Choose start date:");
        System.out.println("1) " + d3);
        System.out.println("2) " + d4);
        System.out.println("3) " + d5);
        System.out.println("4) Enter manually (YYYY-MM-DD)");
        System.out.print("Choose: ");

        String c = readInput(sc);

        return switch (c) {
            case "1" -> d3;
            case "2" -> d4;
            case "3" -> d5;

            case "4" -> {
                System.out.print("Enter date: ");
                LocalDate manual = readDate(sc);

                if (ChronoUnit.DAYS.between(today, manual) < 3) {
                    System.out.println("⚠ Must book at least 3 days in advance.");
                    yield askStartDate(sc);
                }
                yield manual;
            }

            default -> {
                System.out.println("Invalid choice.");
                yield askStartDate(sc);
            }
        };
    }

    // ============================================================
    // SAFE DATE PARSER
    // ============================================================
    private LocalDate readDate(Scanner sc) {
        while (true) {
            String input = readInput(sc);

            try {
                return LocalDate.parse(input);
            } catch (Exception e) {
                System.out.println("❌ Invalid date format! Expected YYYY-MM-DD.");
                System.out.print("Try again: ");
            }
        }
    }

    // ============================================================
    // MAKE BOOKING
    // ============================================================
    private void makeBooking(Scanner sc) {
        String cid = askExistingCustomerId(sc);

        if (cid == null) {
            System.out.println("Booking cancelled.");
            return;
        }

        System.out.println();
        System.out.println("=============================================");
        System.out.println("MAKE BOOKING");
        System.out.println("=============================================");

        System.out.println("Book by → 1) Category   2) Vehicle ID");
        String mode = readInput(sc);

        LocalDate start = askStartDate(sc);

        if (ChronoUnit.DAYS.between(LocalDate.now(), start) < 3) {
            System.out.println("⚠ Minimum 3-day advance required!");
            return;
        }

        System.out.print("Number of days: ");
        int days = Integer.parseInt(readInput(sc));

        System.out.print("Estimated kilometers: ");
        int km = Integer.parseInt(readInput(sc));

        if (mode.equals("1")) {
            Category cat = askCategory(sc);
            var b = system.bookByCategory(cid, cat, start, days, km);
            System.out.println("✔ Booked! ID: " + b.getBookingId());
            return;
        }

        // Book by specific vehicle
        System.out.println("Available vehicles:");
        system.listVehicles().stream()
                .filter(v -> v.getAvailabilityStatus() == AvailabilityStatus.AVAILABLE)
                .sorted(Comparator.comparing(Vehicle::getVehicleId))
                .forEach(v -> System.out.println(" - " + v.getVehicleId() + " | " + v.getModel()));

        String vid = askValidVehicleId(sc);

        var b = system.bookSpecific(cid, vid, start, days, km);

        System.out.println("✔ Booking complete! ID: " + b.getBookingId());
        System.out.println("Deposit charged: LKR 5000");
    }

    // ============================================================
    // UPDATE BOOKING
    // ============================================================
    private void updateBooking(Scanner sc) {
        System.out.println("=============================================");
        System.out.println("UPDATE BOOKING");
        System.out.println("=============================================");

        String id = askValidBookingId(sc);

        LocalDate newStart = null;
        while (true) {
            System.out.print("New start date (YYYY-MM-DD or blank to skip): ");
            String s = readInput(sc);

            if (s.isBlank()) break;

            try {
                LocalDate d = LocalDate.parse(s);

                long diff = ChronoUnit.DAYS.between(LocalDate.now(), d);
                if (diff < 3) {
                    System.out.println("❌ Start date must be at least 3 days ahead.");
                    continue;
                }

                newStart = d;
                break;

            } catch (Exception e) {
                System.out.println("❌ Invalid date! Format must be YYYY-MM-DD.");
            }
        }

        System.out.print("New number of days (blank = no change): ");
        String dd = readInput(sc);

        System.out.print("New total km (blank = no change): ");
        String dk = readInput(sc);

        Booking b = system.updateBooking(
                id,
                newStart,
                dd.isBlank() ? null : Integer.parseInt(dd),
                dk.isBlank() ? null : Integer.parseInt(dk)
        );

        System.out.println("✔ Booking updated: " + b.getBookingId());
    }

    // ============================================================
    // CANCEL BOOKING
    // ============================================================
    private void cancelBooking(Scanner sc) {
        System.out.println("=============================================");
        System.out.println("CANCEL BOOKING");
        System.out.println("=============================================");

        String id = askValidBookingId(sc);

        system.cancelBooking(id);

        System.out.println("✔ Booking successfully cancelled.");
    }

    // ============================================================
    // SEARCH BOOKINGS
    // ============================================================
    private void searchBookings(Scanner sc) {
        System.out.println("=============================================");
        System.out.println("SEARCH BOOKING");
        System.out.println("=============================================");
        System.out.print("Enter booking ID or customer name: ");

        String q = readInput(sc);

        var list = system.searchBookingsByNameOrId(q);

        if (list.isEmpty()) {
            System.out.println("No results found.");
            return;
        }

        list.forEach(System.out::println);
    }

    // ============================================================
    // VIEW BOOKINGS BY DATE
    // ============================================================
    private void viewBookingsByDate(Scanner sc) {
        System.out.println("=============================================");
        System.out.println("VIEW BOOKINGS BY DATE");
        System.out.println("=============================================");

        System.out.print("Enter date (YYYY-MM-DD): ");
        LocalDate date = readDate(sc);

        var list = system.viewBookingsByDate(date);

        if (list.isEmpty()) {
            System.out.println("No bookings for " + date);
        } else {
            list.forEach(System.out::println);
        }
    }

    // ============================================================
    // COMPLETE BOOKING
    // ============================================================
    private void completeAndInvoice(Scanner sc) {
        System.out.println("=============================================");
        System.out.println("COMPLETE BOOKING");
        System.out.println("=============================================");

        String id = askValidBookingId(sc);

        try {
            var invoice = system.completeAndInvoice(id);
            System.out.println(invoice);

        } catch (Exception e) {
            System.out.println("❌ " + e.getMessage());
        }
    }

    // ============================================================
    // LIST VEHICLES
    // ============================================================
    private void listVehicles() {
        List<Vehicle> vehicles = system.listVehicles();
        vehicles.sort(Comparator.comparing(Vehicle::getVehicleId));

        System.out.println("=============================================");
        System.out.println("ALL VEHICLES (Sorted by ID)");
        System.out.println("=============================================");
        vehicles.forEach(System.out::println);
    }
}
