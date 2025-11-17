package com.ecoride.cli;

import com.ecoride.domain.*;
import com.ecoride.service.CarRentalSystem;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

public class ConsoleUI {

    private final CarRentalSystem system;

    public ConsoleUI(CarRentalSystem system) { this.system = system; }

    // =============================================
    // GLOBAL SAFE INPUT with BACK (#)
    // =============================================
    private String readInput(Scanner sc) {
        String input = sc.nextLine().trim();
        if (input.equals("#")) {
            System.out.println("↩ Returning to main menu...");
            System.out.println();
            System.out.println("---------------------------------------------");
            System.out.println("|=== EcoRide Car Rental System (Console) ===|");
            System.out.println("---------------------------------------------");
            throw new RuntimeException("BACK_TO_HOME");
        }
        return input;
    }

    // =============================================
    // MAIN LOOP
    // =============================================
    public void start() {
        System.out.println();
        System.out.println("---------------------------------------------");
        System.out.println("|=== EcoRide Car Rental System (Console) ===|");
        System.out.println("---------------------------------------------");
        Scanner sc = new Scanner(System.in);

        while (true) {
            printMenu();

            try {
                String choice = readInput(sc);

                switch (choice) {
                    case "1" -> registerCustomer(sc);
                    case "2" -> {
                        if (requireAdmin(sc)) {
                            vehicleManagement(sc);
                        }
                    }
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
                if ("BACK_TO_HOME".equals(ex.getMessage())) {
                    continue;  // immediately return to menu
                }
                System.out.println("Error: " + ex.getMessage());
            }
        }
    }

    // =============================================
    // ADMIN AUTH
    // =============================================
    private boolean requireAdmin(Scanner sc) {
        System.out.println();
        System.out.println("---------------------------------");
        System.out.println("| Admin authentication required |");
        System.out.println("---------------------------------");
        System.out.println();
        System.out.print("Admin ID: ");
        String id = readInput(sc);
        System.out.print("Password: ");
        String pwd = readInput(sc);
        boolean ok = system.authenticateAdmin(id, pwd);

        if (!ok) {
            System.out.println("Access denied: admin authentication failed.");
        }
        return ok;
    }

    private void printMenu() {
        System.out.println();
        System.out.println("1) Register customer");
        System.out.println("2) Vehicle management");
        System.out.println("3) Book car");
        System.out.println("4) Update booking (<= 2 days)");
        System.out.println("5) Cancel booking (<= 2 days)");
        System.out.println("6) Search bookings (by name or ID)");
        System.out.println("7) View bookings by start date");
        System.out.println("8) Complete booking & generate invoice");
        System.out.println("9) List vehicles");
        System.out.println("0) Exit");
        System.out.println("(Tip: Enter '#' anytime to return to the main menu)");
        System.out.println();
        System.out.print("Choose: ");
    }

    // =============================================
    // CUSTOMER REGISTRATION
    // =============================================
    private void registerCustomer(Scanner sc) {
        System.out.println();
        System.out.println("=============================================");
        System.out.println("Register customer ");
        System.out.println("=============================================");
        System.out.println();
        System.out.println("Customer type: 1) Local  2) Foreign");

        String type;
        while (true) {
            System.out.print("Choose (1 or 2): ");
            type = readInput(sc);
            if (type.equals("1") || type.equals("2")) break;
            System.out.println("❌ Invalid choice. Please enter 1 for Local or 2 for Foreign.");
        }

        System.out.print("Name: ");
        String name = readInput(sc);

        System.out.print("Contact number: ");
        String contact = readInput(sc);

        System.out.print("Email: ");
        String email = readInput(sc);

        switch (type) {
            case "1" -> {
                System.out.print("NIC: ");
                String nic = readInput(sc);
                system.addCustomer(new LocalCustomer(nic, name, contact, email));
            }
            case "2" -> {
                System.out.print("Passport: ");
                String pass = readInput(sc);
                System.out.print("Nationality: ");
                String nat = readInput(sc);
                system.addCustomer(new ForeignCustomer(pass, nat, name, contact, email));
            }
        }
        System.out.println("✔ Customer successfully registered.");
    }

    // =============================================
    // VEHICLE MANAGEMENT
    // =============================================
    private void vehicleManagement(Scanner sc) {
        while (true) {
            System.out.println();
            System.out.println("=============================================");
            System.out.println("VEHICLE MANAGEMENT ");
            System.out.println("=============================================");
            System.out.println();
            System.out.println("Vehicle management: a) Add  b) Update status  c) Remove  d) Back");
            System.out.print("Choose: ");
            String c = readInput(sc).toLowerCase();

            switch (c) {
                case "a" -> { handleAddVehicle(sc); return; }
                case "b" -> {
                    System.out.print("Vehicle ID: ");
                    String id = readInput(sc);
                    AvailabilityStatus st = askAvailabilityStatus(sc);
                    system.changeAvailability(id, st);
                    System.out.println("✔ Status updated.");
                    return;
                }
                case "c" -> {
                    System.out.print("Vehicle ID: ");
                    String id = readInput(sc);
                    system.removeVehicle(id);
                    System.out.println("✔ Vehicle removed.");
                    return;
                }
                case "d" -> { return; }
                default -> System.out.println("❌ Invalid option. Please enter a, b, c, or d.");
            }
        }
    }

    private void handleAddVehicle(Scanner sc) {
        System.out.println();
        System.out.println("Type: 1) HYBRID  2) ELECTRIC  3) LUXURY_SUV  4) COMPACT_PETROL");

        String t;
        while (true) {
            System.out.print("Choose (1-4): ");
            t = readInput(sc);
            if (t.matches("[1-4]")) break;
            System.out.println("❌ Invalid type.");
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
                    System.out.print("Charge time h: ");
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
            System.out.println("✔ Vehicle added successfully. ID: " + id);

        } catch (Exception ex) {
            System.out.println("❌ Invalid input. Vehicle not added.");
        }
    }

    // =============================================
    // CATEGORY SELECTOR
    // =============================================
    private Category askCategory(Scanner sc) {
        System.out.println();
        System.out.println("=== CATEGORY SELECTOR === ");
        System.out.println();
        System.out.println("Category: 1) COMPACT_PETROL  2) HYBRID  3) ELECTRIC  4) LUXURY_SUV");
        String s = readInput(sc);
        return switch (s) {
            case "1" -> Category.COMPACT_PETROL;
            case "2" -> Category.HYBRID;
            case "3" -> Category.ELECTRIC;
            case "4" -> Category.LUXURY_SUV;
            default -> throw new IllegalArgumentException("Invalid category choice");
        };
    }

    // =============================================
    // CUSTOMER FIND
    // =============================================
    private String askExistingCustomerId(Scanner sc) {
        while (true) {
            System.out.print("Customer NIC/Passport: ");
            String cid = readInput(sc);

            if (cid.isBlank()) {
                System.out.println("NIC/Passport cannot be blank.");
                continue;
            }

            var c = system.findCustomer(cid);
            if (c.isPresent()) {
                var found = c.get();
                System.out.println("✔ Customer found: " + found.getName() + " (" + found.getCustomerId() + ")");
                return cid;
            }

            System.out.println("❌ Customer not found.");
            System.out.println("(R)egister now | (T)ry again | Any other key to cancel");
            String opt = readInput(sc).toLowerCase();

            if (opt.equals("r")) {
                registerCustomer(sc);
            } else if (opt.equals("t")) {
                continue;
            } else {
                return null;
            }
        }
    }

    // =============================================
    // AVAILABILITY STATUS
    // =============================================
    private AvailabilityStatus askAvailabilityStatus(Scanner sc) {
        while (true) {
            System.out.println("New status (1) AVAILABLE | 2) RESERVED | 3) UNDER_MAINTENANCE");
            System.out.print("Choose 1-3: ");
            String s = readInput(sc);

            switch (s) {
                case "1": return AvailabilityStatus.AVAILABLE;
                case "2": return AvailabilityStatus.RESERVED;
                case "3": return AvailabilityStatus.UNDER_MAINTENANCE;
            }
            System.out.println("Invalid choice.");
        }
    }

    // =============================================
    // FRIENDLY DATE SELECTOR
    // =============================================
    private LocalDate askStartDate(Scanner sc) {
        LocalDate today = LocalDate.now();

        LocalDate d3 = today.plusDays(3);
        LocalDate d4 = today.plusDays(4);
        LocalDate d5 = today.plusDays(5);

        System.out.println("Select start date:");
        System.out.println("1) " + d3 + "  (Today + 3 days)");
        System.out.println("2) " + d4 + "  (Today + 4 days)");
        System.out.println("3) " + d5 + "  (Today + 5 days)");
        System.out.println("4) Enter manually (YYYY-MM-DD)");
        System.out.print("Choose: ");

        String c = readInput(sc);
        switch (c) {
            case "1": return d3;
            case "2": return d4;
            case "3": return d5;
            case "4":
                System.out.print("Enter date (YYYY-MM-DD): ");
                LocalDate manual = readDate(sc);

                long diff = ChronoUnit.DAYS.between(today, manual);
                if (diff < 3) {
                    System.out.println("⚠ Booking must be at least 3 days in advance.");
                    return askStartDate(sc);
                }
                return manual;

            default:
                System.out.println("Invalid choice.");
                return askStartDate(sc);
        }
    }

    // =============================================
    // SAFE DATE PARSER
    // =============================================
    private LocalDate readDate(Scanner sc) {
        while (true) {
            String input = readInput(sc);
            try {
                return LocalDate.parse(input);
            } catch (Exception e) {
                System.out.println("❌ Invalid date format. Use YYYY-MM-DD (e.g., 2025-12-10)");
                System.out.print("Try again: ");
            }
        }
    }

    // =============================================
    // MAKE BOOKING
    // =============================================
    private void makeBooking(Scanner sc) {
        String cid = askExistingCustomerId(sc);
        if (cid == null) {
            System.out.println("Booking cancelled.");
            return;
        }

        System.out.println();
            System.out.println("=============================================");
            System.out.println("MAKE BOOKING ");
            System.out.println("=============================================");
            System.out.println();
        System.out.println("Book by: 1) Category  2) Vehicle ID");
        String mode = readInput(sc);

        LocalDate start = askStartDate(sc);
        long daysAhead = ChronoUnit.DAYS.between(LocalDate.now(), start);

        if (daysAhead < 3) {
            System.out.println("⚠ Booking must be at least 3 days in advance.");
            return;
        }

        System.out.print("Number of days: ");
        int days = Integer.parseInt(readInput(sc));

        System.out.print("Total kilometers (estimate): ");
        int km = Integer.parseInt(readInput(sc));

        if ("1".equals(mode)) {
            Category cat = askCategory(sc);
            var b = system.bookByCategory(cid, cat, start, days, km);
            System.out.println("✔ Booked. Booking ID: " + b.getBookingId());
        } else {
            System.out.println("Available vehicles:");
            system.listVehicles().stream()
                    .filter(v -> v.getAvailabilityStatus() == AvailabilityStatus.AVAILABLE)
                    .sorted(Comparator.comparing(Vehicle::getVehicleId))
                    .forEach(v ->
                            System.out.println(" - " + v.getVehicleId() + " | " + v.getModel() + " | " + v.getCategory())
                    );

            System.out.print("Vehicle ID: ");
            String vid = readInput(sc);

            var b = system.bookSpecific(cid, vid, start, days, km);
            System.out.println("✔ Booked. Booking ID: " + b.getBookingId());
        }

        System.out.println("Deposit charged: LKR 5000");
    }

    // =============================================
    // UPDATE BOOKING
    // =============================================
    private void updateBooking(Scanner sc) {
        System.out.println("=============================================");
        System.out.println("UPDATE BOOKING ");
        System.out.println("=============================================");
        System.out.println();
        System.out.print("Booking ID: ");
        String id = readInput(sc);

        System.out.println("Select new start date (or press Enter to skip):");
        String input = readInput(sc);

        LocalDate newStart = input.isBlank() ? null : askStartDate(sc);

        System.out.print("New number of days (blank = no change): ");
        String dd = readInput(sc);

        System.out.print("New total km (blank = no change): ");
        String dk = readInput(sc);

        var b = system.updateBooking(
                id,
                newStart,
                dd.isBlank() ? null : Integer.parseInt(dd),
                dk.isBlank() ? null : Integer.parseInt(dk)
        );

        System.out.println("✔ Updated booking: " + b.getBookingId());
    }

    // =============================================
    // CANCEL BOOKING
    // =============================================
    private void cancelBooking(Scanner sc) {
        System.out.println("=============================================");
        System.out.println("CANCEL BOOKING ");
        System.out.println("=============================================");
        System.out.println();
        System.out.print("Booking ID: ");
        String id = readInput(sc);
        system.cancelBooking(id);
        System.out.println("✔ Booking cancelled.");
    }

    // =============================================
    // SEARCH BOOKINGS
    // =============================================
    private void searchBookings(Scanner sc) {
        System.out.println("=============================================");
        System.out.println("SEARCH BOOKING ");
        System.out.println("=============================================");
        System.out.println();
        System.out.print("Enter booking ID or customer name): ");
        String q = readInput(sc);

        List<Booking> list = system.searchBookingsByNameOrId(q);
        if (list.isEmpty()) System.out.println("No results.");
        else list.forEach(System.out::println);
    }

    // =============================================
    // VIEW BY DATE
    // =============================================
    private void viewBookingsByDate(Scanner sc) {
        System.out.println("=============================================");
        System.out.println("VIEW BOOKINGS BY DATE ");
        System.out.println("=============================================");
        System.out.println();
        System.out.print("Enter date (YYYY-MM-DD): ");
        LocalDate date = readDate(sc);

        var list = system.viewBookingsByDate(date);
        if (list.isEmpty()) {
            System.out.println("No bookings for " + date);
        } else {
            System.out.println("Bookings for " + date + ":");
            list.forEach(System.out::println);
        }
    }

    // =============================================
    // COMPLETE BOOKING
    // =============================================
    private void completeAndInvoice(Scanner sc) {
        System.out.println("=============================================");
        System.out.println("COMPLETE BOOKING ");
        System.out.println("=============================================");
        System.out.println();
        System.out.print("Booking ID: ");
        String id = readInput(sc);

        try {
            var invoice = system.completeAndInvoice(id);
            System.out.println(invoice);
        } catch (Exception e) {
            System.out.println("❌ " + e.getMessage());
        }
    }

    // =============================================
    // LIST VEHICLES
    // =============================================
    private void listVehicles() {
        List<Vehicle> vehicles = system.listVehicles();
        vehicles.sort(Comparator.comparing(Vehicle::getVehicleId));

        System.out.println("=============================================");
        System.out.println("Vehicle List (Sorted by Vehicle ID) ");
        System.out.println("=============================================");
        System.out.println();
        vehicles.forEach(System.out::println);
    }
}
