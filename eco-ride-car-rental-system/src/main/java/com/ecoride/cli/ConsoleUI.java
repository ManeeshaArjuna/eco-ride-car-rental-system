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

    public void start() {
        System.out.println("=== EcoRide Car Rental System (Console) ===");
        Scanner sc = new Scanner(System.in);
        while (true) {
            printMenu();
            String choice = sc.nextLine().trim();
            try {
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
            } catch (Exception ex) {
                System.out.println("Error: " + ex.getMessage());
            }
        }
    }

    /** Prompt for admin credentials and verify against the system's admin store. */
     private boolean requireAdmin(Scanner sc) {
         System.out.println("[Admin authentication required]");
         System.out.print("Admin ID: ");
         String id = sc.nextLine().trim();
         System.out.print("Password: ");
         String pwd = sc.nextLine().trim();
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
        System.out.print("Choose: ");
    }

    private void registerCustomer(Scanner sc) {
        System.out.println("Customer type: 1) Local  2) Foreign");
        String t = sc.nextLine().trim();
        System.out.print("Name: ");
        String name = sc.nextLine().trim();
        System.out.print("Contact: ");
        String contact = sc.nextLine().trim();
        System.out.print("Email: ");
        String email = sc.nextLine().trim();
        switch (t) {
            case "1" -> {
                System.out.print("NIC: ");
                String nic = sc.nextLine().trim();
                system.addCustomer(new LocalCustomer(nic, name, contact, email));
            }
            case "2" -> {
                System.out.print("Passport: ");
                String pass = sc.nextLine().trim();
                System.out.print("Nationality: ");
                String nat = sc.nextLine().trim();
                system.addCustomer(new ForeignCustomer(pass, nat, name, contact, email));
            }
            default -> System.out.println("Invalid type.");
        }
        System.out.println("Customer registered.");
    }

    private void vehicleManagement(Scanner sc) {
        System.out.println("Vehicle management: a) Add  b) Update status  c) Remove");
        String c = sc.nextLine().trim();
        switch (c) {
        case "a" -> {
            System.out.println("Type: 1) HYBRID  2) ELECTRIC  3) LUXURY_SUV  4) COMPACT_PETROL");
            String t = sc.nextLine().trim();
            switch (t) {
                case "1" -> {
                    String id = system.generateVehicleId();
                    System.out.println("Assigned Vehicle ID: " + id);
                    System.out.print("Model: "); String model = sc.nextLine().trim();
                    System.out.print("Battery kWh: "); double b = Double.parseDouble(sc.nextLine().trim());
                    System.out.print("Efficiency km/l: "); double e = Double.parseDouble(sc.nextLine().trim());
                    system.addVehicle(new HybridCar(id, model, b, e));
                    System.out.println("Added. ID: " + id);
                }
                case "2" -> {
                    String id = system.generateVehicleId();
                    System.out.println("Assigned Vehicle ID: " + id);
                    System.out.print("Model: "); String model = sc.nextLine().trim();
                    System.out.print("Battery kWh: "); double b = Double.parseDouble(sc.nextLine().trim());
                    System.out.print("Charge time h: "); double h = Double.parseDouble(sc.nextLine().trim());
                    system.addVehicle(new ElectricCar(id, model, b, h));
                    System.out.println("Added. ID: " + id);
                }
                case "3" -> {
                    String id = system.generateVehicleId();
                    System.out.println("Assigned Vehicle ID: " + id);
                    System.out.print("Model: "); String model = sc.nextLine().trim();
                    System.out.print("Features: "); String f = sc.nextLine().trim();
                    System.out.print("Driver included (true/false): "); boolean d = Boolean.parseBoolean(sc.nextLine().trim());
                    system.addVehicle(new LuxurySUVCar(id, model, f, d));
                    System.out.println("Added. ID: " + id);
                }
                case "4" -> {
                    String id = system.generateVehicleId();
                    System.out.println("Assigned Vehicle ID: " + id);
                    System.out.print("Model: "); String model = sc.nextLine().trim();
                    System.out.print("Engine capacity (L): "); double cap = Double.parseDouble(sc.nextLine().trim());
                    System.out.print("Transmission (AUTO/MANUAL): "); String tr = sc.nextLine().trim();
                    system.addVehicle(new CompactPetrolCar(id, model, cap, tr));
                    System.out.println("Added. ID: " + id);
                }
                default -> System.out.println("Invalid type.");
            }
            }
            case "b" -> {
                System.out.print("Vehicle ID: "); String id = sc.nextLine().trim();
                AvailabilityStatus st = askAvailabilityStatus(sc);
                system.changeAvailability(id, st);
                System.out.println("Updated.");
            }
            case "c" -> {
                System.out.print("Vehicle ID: "); String id = sc.nextLine().trim();
                system.removeVehicle(id);
                System.out.println("Removed.");
            }
            default -> {}
        }
    }

    private Category askCategory(Scanner sc) {
        System.out.println("Category: 1) COMPACT_PETROL  2) HYBRID  3) ELECTRIC  4) LUXURY_SUV");
        String s = sc.nextLine().trim();
        return switch (s) {
            case "1" -> Category.COMPACT_PETROL;
            case "2" -> Category.HYBRID;
            case "3" -> Category.ELECTRIC;
            case "4" -> Category.LUXURY_SUV;
            default -> throw new IllegalArgumentException("Invalid category choice");
        };
    }

private String askExistingCustomerId(Scanner sc) {
    while (true) {
        System.out.print("Customer NIC/Passport: ");
        String cid = sc.nextLine().trim();

        if (cid.isBlank()) {
            System.out.println("NIC/Passport cannot be blank.");
            continue;
        }

        var customerOpt = system.findCustomer(cid);

        if (customerOpt.isPresent()) {
            var c = customerOpt.get();
            System.out.println("✔ Customer found: " + c.getName() + " (" + c.getCustomerId() + ")");
            return cid; // valid customer — immediately confirmed!
        }

        // Not found → options
        System.out.println("❌ Customer not found.");
        System.out.println("Options: (R)egister now  |  (T)ry again  |  any other key to cancel");
        String choice = sc.nextLine().trim().toLowerCase();

        if ("r".equals(choice)) {
            registerCustomer(sc);
        } else if ("t".equals(choice)) {
            // loop again
        } else {
            return null;
        }
    }
}

    private AvailabilityStatus askAvailabilityStatus(Scanner sc) {
    while (true) {
        System.out.println("New status (1) AVAILABLE | 2) RESERVED | 3) UNDER_MAINTENANCE");
        System.out.print("Choose 1-3: ");
        String s = sc.nextLine().trim();
        switch (s) {
            case "1": return AvailabilityStatus.AVAILABLE;
            case "2": return AvailabilityStatus.RESERVED;
            case "3": return AvailabilityStatus.UNDER_MAINTENANCE;
            default:
                System.out.println("Invalid choice. Please enter 1, 2, or 3.");
        }
    }
}

// -----------------------------------------------------------
// Friendly date selector for booking start date
// -----------------------------------------------------------
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

    String c = sc.nextLine().trim();
    switch (c) {
        case "1": return d3;
        case "2": return d4;
        case "3": return d5;
        case "4":
            System.out.print("Enter date (YYYY-MM-DD): ");
            LocalDate manual = readDate(sc);  // safe date parser
            long diff = ChronoUnit.DAYS.between(today, manual);
            if (diff < 3) {
                System.out.println("⚠ Booking must be at least 3 days in advance.");
                return askStartDate(sc);
            }
            return manual;

        default:
            System.out.println("Invalid choice. Please try again.");
            return askStartDate(sc);
    }
}


// -----------------------------------------------------------
// Safe date parser to avoid ugly Java parsing errors
// -----------------------------------------------------------
private LocalDate readDate(Scanner sc) {
    while (true) {
        String input = sc.nextLine().trim();
        try {
            return LocalDate.parse(input);
        } catch (Exception e) {
            System.out.println("❌ Invalid date format! Please use YYYY-MM-DD (e.g., 2025-12-10)");
            System.out.print("Try again: ");
        }
    }
}


    private void makeBooking(Scanner sc) {
        String cid = askExistingCustomerId(sc);
        if (cid == null) {
            System.out.println("Booking cancelled.");
            return;
        }

        System.out.println("Book by: 1) Category  2) Vehicle ID");
        String mode = sc.nextLine().trim();
        System.out.print("Start date (YYYY-MM-DD): ");
        LocalDate start = askStartDate(sc);

        long daysAhead = ChronoUnit.DAYS.between(LocalDate.now(), start);
        if (daysAhead < 3) {
            System.out.println("⚠ Booking must be at least 3 days in advance.");
            System.out.println("Please enter a new valid start date.");
            return;   // stop the booking flow early
        }

        System.out.print("Number of days: ");
        int days = Integer.parseInt(sc.nextLine().trim());
        System.out.print("Total kilometers (estimate): ");
        int km = Integer.parseInt(sc.nextLine().trim());

        if ("1".equals(mode)) {
            Category cat = askCategory(sc);
            var b = system.bookByCategory(cid, cat, start, days, km);
            System.out.println("Booked. ID: " + b.getBookingId() + " | Vehicle " + b.getVehicle().getVehicleId());
        } else {
            // Show available vehicles BEFORE asking for ID
            System.out.println("Available vehicles:");
            system.listVehicles().stream()
                    .filter(v -> v.getAvailabilityStatus() == AvailabilityStatus.AVAILABLE)
                    .sorted(Comparator.comparing(Vehicle::getVehicleId))
                    .forEach(v -> System.out.println(" - " + v.getVehicleId() + " | " + v.getModel() + " | " + v.getCategory()));

            System.out.print("Vehicle ID: ");
            String vid = sc.nextLine().trim();

            var b = system.bookSpecific(cid, vid, start, days, km);
            System.out.println("Booked. ID: " + b.getBookingId() + " | Vehicle " + b.getVehicle().getVehicleId());
        }
        System.out.println("Deposit charged: LKR 5000");
    }

    private void updateBooking(Scanner sc) {
        System.out.print("Booking ID: ");
        String id = sc.nextLine().trim();

        // Optional start date update
        System.out.println("Select new start date (or press Enter to skip):");
        String input = sc.nextLine().trim();

        LocalDate newStart;
        if (input.isBlank()) {
            newStart = null;                     // keep existing
        } else {
            newStart = askStartDate(sc);         // show selector only if needed
        }

        // Optional days
        System.out.print("New number of days (or blank to keep existing): ");
        String dd = sc.nextLine().trim();

        // Optional mileage
        System.out.print("New total km (or blank to keep existing): ");
        String dk = sc.nextLine().trim();

        var b = system.updateBooking(
                id,
                newStart,
                dd.isBlank() ? null : Integer.parseInt(dd),
                dk.isBlank() ? null : Integer.parseInt(dk));

        System.out.println("Updated: " + b.getBookingId());
    }

    private void cancelBooking(Scanner sc) {
        System.out.print("Booking ID: ");
        String id = sc.nextLine().trim();
        system.cancelBooking(id);
        System.out.println("Cancelled.");
    }

    private void searchBookings(Scanner sc) {
        System.out.print("Query (booking ID or customer name): ");
        String q = sc.nextLine().trim();
        List<Booking> list = system.searchBookingsByNameOrId(q);
        if (list.isEmpty()) System.out.println("No results.");
        else list.forEach(System.out::println);
    }

    private void viewBookingsByDate(Scanner sc) {
        System.out.print("Enter date (YYYY-MM-DD): ");
        LocalDate date = readDate(sc);  // safe input

        var list = system.viewBookingsByDate(date);

        if (list.isEmpty()) {
            System.out.println("No bookings for " + date);
            return;
        }

        System.out.println("Bookings for " + date + ":");
        list.forEach(System.out::println);
    }


    private void completeAndInvoice(Scanner sc) {
        System.out.print("Booking ID: ");
        String id = sc.nextLine().trim();
        try {
            var invoice = system.completeAndInvoice(id);
            System.out.println(invoice);
        } catch (Exception e) {
            System.out.println("❌ " + e.getMessage());
        }
    }

    private void listVehicles() {
    List<Vehicle> vehicles = system.listVehicles();

    // Sort by vehicle ID in ascending order
    vehicles.sort(Comparator.comparing(Vehicle::getVehicleId));

    System.out.println("=== Vehicle List (Sorted by Vehicle ID) ===");
    for (Vehicle v : vehicles) {
        System.out.println(v);
    }
}

}
