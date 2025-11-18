package com.ecoride.cli;

import com.ecoride.domain.*;
import com.ecoride.service.CarRentalSystem;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class ConsoleUI {

    private final CarRentalSystem system;

    // ============================================================
    // COLOR & ICON CONSTANTS (Theme A - Eco Green)
    // ============================================================
    private static final String RESET        = "\u001B[0m";
    private static final String GREEN        = "\u001B[32m";
    private static final String BRIGHT_GREEN = "\u001B[92m";
    private static final String YELLOW       = "\u001B[33m";
    private static final String CYAN         = "\u001B[36m";
    private static final String RED          = "\u001B[31m";
    private static final String NAVY_BLUE = "\u001B[34m";
    private static final String GREY         = "\u001B[90m";
    private static final String BOLD         = "\u001B[1m";
    private static final String BELL         = "\u0007";

    private static final String ICON_OK      = "✔";
    private static final String ICON_ERR     = "❌";
    private static final String ICON_WARN    = "⚠";
    private static final String ICON_HOME    = "🏠";
    private static final String ICON_USER    = "👤";
    private static final String ICON_CAR     = "🚗";
    private static final String ICON_BOOKING = "📅";
    private static final String ICON_INVOICE = "🧾";
    private static final String ICON_KEY     = "🔑";

    public ConsoleUI(CarRentalSystem system) {
        this.system = system;
    }

    // ============================================================
    // SMALL UI HELPERS
    // ============================================================
    private void printHeader(String title, String icon) {
        String line = GREEN + "==================================================" + RESET;
        System.out.println();
        System.out.println(line);
        System.out.println(BRIGHT_GREEN + icon + " " + title + RESET);
        System.out.println(line);
    }

    private void printSectionTitle(String title) {
        System.out.println();
        System.out.println(NAVY_BLUE + "---- " + title + " ----" + RESET);

    }

    private void printError(String msg) {
        System.out.println(RED + ICON_ERR + " " + msg + RESET + BELL);
    }

    private void printWarn(String msg) {
        System.out.println(YELLOW + ICON_WARN + " " + msg + RESET);
    }

    private void printSuccess(String msg) {
        System.out.println(BRIGHT_GREEN + ICON_OK + " " + msg + RESET);
    }

    // ============================================================
    // GLOBAL SAFE INPUT WITH BACK (#)
    // ============================================================
    private String readInput(Scanner sc) {
        String input = sc.nextLine().trim();

        if (input.equals("#")) {
            System.out.println(YELLOW + ICON_HOME + " Returning to main menu..." + RESET);
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
            System.out.print(CYAN + "Booking ID " + RESET + "(e.g., R-ca4a1d44): ");
            String id = readInput(sc);

            if (!id.matches("R-[0-9a-f]{8}")) {
                printError("Invalid Booking ID format! Expected: R-xxxxxxxx");
                continue;
            }

            Optional<Booking> booking = system.findBookingById(id);

            if (booking.isEmpty()) {
                printError("No booking found with ID: " + id);
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
            System.out.print(CYAN + "Vehicle ID " + RESET + "(e.g., C-001): ");
            String vid = readInput(sc);

            if (!vid.matches("C-\\d{3}")) {
                printError("Invalid Vehicle ID format! Expected: C-001");
                continue;
            }

            boolean exists = system.listVehicles().stream()
                    .anyMatch(v -> v.getVehicleId().equals(vid));

            if (!exists) {
                printError("Vehicle not found: " + vid);
                continue;
            }

            return vid;
        }
    }

    // ============================================================
    // MAIN LOOP WITH ANIMATED WELCOME
    // ============================================================
    public void start() {
        showWelcomeScreen();

        Scanner sc = new Scanner(System.in);

        while (true) {
            printMenu();

            try {
                String choice = readInput(sc);

                switch (choice) {
                    case "1" -> registerCustomer(sc);
                    case "2" -> {
                        if (requireAdmin(sc)) vehicleManagement(sc);
                    }
                    case "3" -> makeBooking(sc);
                    case "4" -> updateBooking(sc);
                    case "5" -> cancelBooking(sc);
                    case "6" -> searchBookings(sc);
                    case "7" -> viewBookingsByDate(sc);
                    case "8" -> completeAndInvoice(sc);
                    case "9" -> listVehicles();
                    case "0" -> {
                        System.out.println(GREY + "Thank you for using EcoRide. Goodbye!" + RESET);
                        return;
                    }
                    default -> printError("Invalid option. Please choose from the menu.");
                }

            } catch (RuntimeException ex) {
                if ("BACK_TO_HOME".equals(ex.getMessage()))
                    continue;
                printError("Unexpected error: " + ex.getMessage());
            }
        }
    }

    private void showWelcomeScreen() {
        System.out.println();
        System.out.println(GREEN + "==================================================" + RESET);
        System.out.println(BRIGHT_GREEN + "      " + ICON_CAR + "  EcoRide Car Rental System  " + ICON_CAR + RESET);
        System.out.println(GREEN + "==================================================" + RESET);
        System.out.println(GREY + "Developed by Maneesha Arjuna - ESOFT UNI KANDY" + RESET);
        System.out.print(GREEN + "Loading" + RESET);

        // simple animated loading
        try {
            for (int i = 0; i < 3; i++) {
                Thread.sleep(300);
                System.out.print(GREEN + "." + RESET);
            }
        } catch (InterruptedException ignored) {
            // ignore
        }
        System.out.println();
        System.out.println(GREY + "(Hint: You can type '#' at any time to go back to the main menu.)" + RESET);
    }

    // ============================================================
    // MAIN MENU
    // ============================================================
    private void printMenu() {
        System.out.println();
        System.out.println(GREEN + "---------------- MAIN MENU ----------------" + RESET);
        System.out.println(NAVY_BLUE + "1)" + RESET + " " + ICON_USER    + " Register customer");

        System.out.println(NAVY_BLUE + "2)" + RESET + " " + ICON_CAR     + " Vehicle management (Admin)");

        System.out.println(NAVY_BLUE + "3)" + RESET + " " + ICON_BOOKING + " Book car");

        System.out.println(NAVY_BLUE + "4)" + RESET + " " + ICON_BOOKING + " Update booking (<= 2 days)");

        System.out.println(NAVY_BLUE + "5)" + RESET + " " + ICON_BOOKING + " Cancel booking (<= 2 days)");

        System.out.println(NAVY_BLUE + "6)" + RESET + " 🔍 Search bookings");

        System.out.println(NAVY_BLUE + "7)" + RESET + " " + ICON_BOOKING + " View bookings by start date");

        System.out.println(NAVY_BLUE + "8)" + RESET + " " + ICON_INVOICE + " Complete booking & generate invoice");

        System.out.println(NAVY_BLUE + "9)" + RESET + " " + ICON_CAR     + " List vehicles");

        System.out.println(NAVY_BLUE + "0)" + RESET + " Exit");

        System.out.println(GREY + "(Tip: Enter '#' anytime to return to main menu)" + RESET);
        System.out.print(NAVY_BLUE + "Choose: " + RESET);

    }

    // ============================================================
    // ADMIN AUTH
    // ============================================================
    private boolean requireAdmin(Scanner sc) {
        printHeader("ADMIN LOGIN", ICON_KEY);

        System.out.print("Admin ID: ");
        String id = readInput(sc);

        System.out.print("Password: ");
        String pwd = readInput(sc);

        boolean ok = system.authenticateAdmin(id, pwd);

        if (!ok) {
            printError("Access denied: invalid admin credentials.");
        } else {
            printSuccess("Admin authenticated.");
        }

        return ok;
    }

    private void registerCustomer(Scanner sc) {
        System.out.println();
        System.out.println(YELLOW + "=============================================" + RESET);
        System.out.println(BOLD + "REGISTER CUSTOMER" + RESET);
        System.out.println(YELLOW + "=============================================" + RESET);

        System.out.println(CYAN + "Customer type: 1) Local  2) Foreign" + RESET);
        String type;

        while (true) {
            System.out.print(CYAN + "Choose (1 or 2): " + RESET);
            type = readInput(sc);

            if (type.equals("1") || type.equals("2")) {
                break;
            }
            System.out.println(RED + "❌ Invalid choice! Enter 1 for Local or 2 for Foreign." + RESET);
        }

        String name     = askName(sc);
        String contact  = askContact(sc);
        String email    = askEmail(sc);

        if ("1".equals(type)) {
            String nic = askNIC(sc);
            system.addCustomer(new LocalCustomer(nic, name, contact, email));
        } else {
            String pass = askPassport(sc);
            String nat  = askNationality(sc);
            system.addCustomer(new ForeignCustomer(pass, nat, name, contact, email));
        }

        System.out.println(GREEN + "✔ Customer registered successfully!" + RESET);
    }


    // ============================================================
    // VEHICLE MANAGEMENT
    // ============================================================
    private void vehicleManagement(Scanner sc) {
        while (true) {
            printHeader("VEHICLE MANAGEMENT", ICON_CAR);

            System.out.println("a) Add   b) Update status   c) Remove   d) Back");
            System.out.print("Choose: ");

            String c = readInput(sc).toLowerCase();

            switch (c) {
                case "a" -> {
                    handleAddVehicle(sc);
                    return;
                }
                case "b" -> {
                    String id = askValidVehicleId(sc);
                    AvailabilityStatus st = askAvailabilityStatus(sc);
                    system.changeAvailability(id, st);
                    printSuccess("Vehicle status updated.");
                    return;
                }
                case "c" -> {
                    String id = askValidVehicleId(sc);
                    system.removeVehicle(id);
                    printSuccess("Vehicle removed.");
                    return;
                }
                case "d" -> { return; }
                default -> printError("Invalid choice! Please select a, b, c, or d.");
            }
        }
    }

    // ============================================================
    // ADD VEHICLE
    // ============================================================
    private void handleAddVehicle(Scanner sc) {
        printSectionTitle("Add new vehicle");

        System.out.println("Vehicle Type:");
        System.out.println("  1) HYBRID");
        System.out.println("  2) ELECTRIC");
        System.out.println("  3) LUXURY_SUV");
        System.out.println("  4) COMPACT_PETROL");

        String t;
        while (true) {
            System.out.print("Choose (1-4): ");
            t = readInput(sc);
            if (t.matches("[1-4]")) break;
            printError("Invalid type. Please choose 1-4.");
        }

        String id = system.generateVehicleId();
        System.out.println(GREY + "Assigned Vehicle ID: " + id + RESET);

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

            printSuccess("Vehicle added successfully! ID: " + id);

        } catch (Exception ex) {
            printError("Invalid numeric input — vehicle not added.");
        }
    }

    // ============================================================
    // CATEGORY SELECTOR
    // ============================================================
    private Category askCategory(Scanner sc) {
        printSectionTitle("Choose vehicle category");
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
                printError("NIC / Passport cannot be blank.");
                continue;
            }

            var c = system.findCustomer(cid);

            if (c.isPresent()) {
                System.out.println(GREEN + ICON_USER + " Found: " + c.get().getName() + RESET);
                return cid;
            }

            printError("Customer not found.");
            System.out.println("(R)egister  |  (T)ry again  |  Other: Cancel");

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

    // ============================================================
    // AVAILABILITY STATUS
    // ============================================================
    private AvailabilityStatus askAvailabilityStatus(Scanner sc) {
        while (true) {
            printSectionTitle("Availability status");
            System.out.println("1) AVAILABLE");
            System.out.println("2) RESERVED");
            System.out.println("3) UNDER_MAINTENANCE");
            System.out.print("Choose: ");

            String s = readInput(sc);

            switch (s) {
                case "1": return AvailabilityStatus.AVAILABLE;
                case "2": return AvailabilityStatus.RESERVED;
                case "3": return AvailabilityStatus.UNDER_MAINTENANCE;
                default:  printError("Invalid choice. Please select 1, 2, or 3.");
            }
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

        printSectionTitle("Start date");
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
                System.out.print("Enter date (YYYY-MM-DD): ");
                LocalDate manual = readDate(sc);

                if (ChronoUnit.DAYS.between(today, manual) < 3) {
                    printWarn("Must book at least 3 days in advance.");
                    yield askStartDate(sc);
                }
                yield manual;
            }
            default -> {
                printError("Invalid choice.");
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
                printError("Invalid date format! Expected YYYY-MM-DD.");
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
            printWarn("Booking cancelled by user.");
            return;
        }

        printHeader("MAKE BOOKING", ICON_BOOKING);

        System.out.println("Book by → 1) Category   2) Vehicle ID");
        String mode = readInput(sc);

        LocalDate start = askStartDate(sc);

        if (ChronoUnit.DAYS.between(LocalDate.now(), start) < 3) {
            printWarn("Minimum 3-day advance required!");
            return;
        }

        System.out.print("Number of days: ");
        int days = Integer.parseInt(readInput(sc));

        System.out.print("Estimated kilometers: ");
        int km = Integer.parseInt(readInput(sc));

        if (mode.equals("1")) {
            Category cat = askCategory(sc);
            Booking b = system.bookByCategory(cid, cat, start, days, km);
            printSuccess("Booked! Booking ID: " + b.getBookingId());
            System.out.println(GREY + "Vehicle: " + b.getVehicle().getVehicleId() + " (" + b.getVehicle().getModel() + ")" + RESET);
            System.out.println(GREY + "Start: " + b.getStartDate() + " | End: " + b.getEndDate() + RESET);
            System.out.println(GREEN + "Deposit charged: LKR 5000" + RESET);
            return;
        }

        // Book by specific vehicle
        printSectionTitle("Available vehicles");
        List<Vehicle> available = system.listVehicles().stream()
                .filter(v -> v.getAvailabilityStatus() == AvailabilityStatus.AVAILABLE)
                .sorted(Comparator.comparing(Vehicle::getVehicleId))
                .toList();

        if (available.isEmpty()) {
            printWarn("No available vehicles at the moment.");
            return;
        }

        printVehicleTable(available);

        String vid = askValidVehicleId(sc);
        Booking b = system.bookSpecific(cid, vid, start, days, km);

        printSuccess("Booking complete! ID: " + b.getBookingId());
        System.out.println(GREEN + "Deposit charged: LKR 5000" + RESET);
    }

    // ============================================================
    // UPDATE BOOKING
    // ============================================================
    private void updateBooking(Scanner sc) {
        printHeader("UPDATE BOOKING", ICON_BOOKING);

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
                    printError("Start date must be at least 3 days ahead.");
                    continue;
                }

                newStart = d;
                break;

            } catch (Exception e) {
                printError("Invalid date! Format must be YYYY-MM-DD.");
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

        printSuccess("Booking updated: " + b.getBookingId());
    }

    // ============================================================
    // CANCEL BOOKING
    // ============================================================
    private void cancelBooking(Scanner sc) {
        printHeader("CANCEL BOOKING", ICON_BOOKING);

        String id = askValidBookingId(sc);
        system.cancelBooking(id);

        printSuccess("Booking successfully cancelled.");
    }

    // ============================================================
    // SEARCH BOOKINGS
    // ============================================================
    private void searchBookings(Scanner sc) {
        printHeader("SEARCH BOOKINGS", "🔍");

        System.out.print("Enter booking ID or customer name: ");
        String q = readInput(sc);

        List<Booking> list = system.searchBookingsByNameOrId(q);

        if (list.isEmpty()) {
            printWarn("No results found.");
        } else {
            printBookingTable(list);
        }
    }

    // ============================================================
    // VIEW BOOKINGS BY DATE
    // ============================================================
    private void viewBookingsByDate(Scanner sc) {
        printHeader("VIEW BOOKINGS BY DATE", ICON_BOOKING);

        System.out.print("Enter date (YYYY-MM-DD): ");
        LocalDate date = readDate(sc);

        List<Booking> list = system.viewBookingsByDate(date);

        if (list.isEmpty()) {
            printWarn("No bookings for " + date);
        } else {
            printBookingTable(list);
        }
    }

    // ============================================================
    // COMPLETE BOOKING
    // ============================================================
    private void completeAndInvoice(Scanner sc) {
        printHeader("COMPLETE BOOKING & INVOICE", ICON_INVOICE);

        String id = askValidBookingId(sc);

        try {
            Invoice invoice = system.completeAndInvoice(id);
            // pretty invoice printing – rely on invoice.toString()
            System.out.println(invoice);

        } catch (Exception e) {
            printError(e.getMessage());
        }
    }

    // ============================================================
    // LIST VEHICLES (TABLE FORMAT)
    // ============================================================
    private void listVehicles() {
        List<Vehicle> vehicles = system.listVehicles();
        vehicles.sort(Comparator.comparing(Vehicle::getVehicleId));

        printHeader("ALL VEHICLES (Sorted by ID)", ICON_CAR);
        if (vehicles.isEmpty()) {
            printWarn("No vehicles in the system.");
            return;
        }
        printVehicleTable(vehicles);
    }

    // ============================================================
    // TABLE HELPERS
    // ============================================================
    private void printVehicleTable(List<Vehicle> vehicles) {
        System.out.printf(
                NAVY_BLUE + "%-6s %-20s %-15s %-18s%n" + RESET,
        
                "ID", "Model", "Category", "Status"
        );
        System.out.println(GREY + "----------------------------------------------------------" + RESET);

        for (Vehicle v : vehicles) {
            System.out.printf(
                    "%-6s %-20s %-15s %-18s%n",
                    v.getVehicleId(),
                    v.getModel(),
                    v.getCategory(),
                    v.getAvailabilityStatus()
            );
        }
    }

    private void printBookingTable(List<Booking> list) {
        System.out.printf(
                NAVY_BLUE + "%-12s %-18s %-8s %-8s %-12s %-10s%n" + RESET,
        
                "Booking ID", "Customer", "Car", "Start", "End", "Status"
        );
        System.out.println(GREY + "---------------------------------------------------------------------" + RESET);

        for (Booking b : list) {
            System.out.printf(
                    "%-12s %-18s %-8s %-8s %-12s %-10s%n",
                    b.getBookingId(),
                    b.getCustomer().getName(),
                    b.getVehicle().getVehicleId(),
                    b.getStartDate(),
                    b.getEndDate(),
                    b.getStatus()
            );
        }
    }

    // ===============================
    // VALIDATED INPUT HELPERS
    // ===============================
    private String askName(Scanner sc) {
        while (true) {
            System.out.print(CYAN + "Name: " + RESET);
            String name = readInput(sc);

            if (!name.matches("[A-Za-z ]+")) {
                System.out.println(RED + "❌ Invalid name! Only letters and spaces are allowed." + RESET);
                continue;
            }
            return name;
        }
    }

    private String askContact(Scanner sc) {
        while (true) {
            System.out.print(CYAN + "Contact number (10 digits): " + RESET);
            String contact = readInput(sc);

            if (!contact.matches("\\d{10}")) {
                System.out.println(RED + "❌ Invalid contact! Please enter exactly 10 digits." + RESET);
                continue;
            }
            return contact;
        }
    }

    private String askEmail(Scanner sc) {
        while (true) {
            System.out.print(CYAN + "Email: " + RESET);
            String email = readInput(sc);

            if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
                System.out.println(RED + "❌ Invalid email format! Example: user@example.com" + RESET);
                continue;
            }
            return email;
        }
    }

    private String askNIC(Scanner sc) {
        while (true) {
            System.out.print(CYAN + "NIC: " + RESET);
            String nic = readInput(sc);

            // Old: 9 digits + V/v/X/x   |  New: 12 digits
            if (!nic.matches("^[0-9]{9}[vVxX]$") && !nic.matches("^[0-9]{12}$")) {
                System.out.println(RED + "❌ Invalid NIC! Use 9 digits + V/X or 12 digits." + RESET);
                continue;
            }
            return nic;
        }
    }

    private String askPassport(Scanner sc) {
        while (true) {
            System.out.print(CYAN + "Passport: " + RESET);
            String pass = readInput(sc);

            if (!pass.matches("^[A-Z0-9]{6,12}$")) {
                System.out.println(RED + "❌ Invalid passport! Use 6–12 characters (A–Z, 0–9)." + RESET);
                continue;
            }
            return pass;
        }
    }

    private String askNationality(Scanner sc) {
        while (true) {
            System.out.print(CYAN + "Nationality: " + RESET);
            String nat = readInput(sc);

            if (!nat.matches("^[A-Za-z ]+$")) {
                System.out.println(RED + "❌ Invalid nationality! Only letters and spaces are allowed." + RESET);
                continue;
            }
            return nat;
        }
    }

}
