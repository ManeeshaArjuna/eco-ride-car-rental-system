# EcoRide Car Rental System — Console (UML‑Aligned)

This project follows your **UML class diagram** structure and implements the **console-based** EcoRide system.
- Java 17 + Maven
- In-memory repositories (ArrayList/Map)
- Booking policy (3‑day lead time, 2‑day amendment/cancel window, deposit LKR 5,000)
- Pricing & invoices per fee table
- JUnit 5 tests
- PlantUML diagram with **`K1234567_`** class name prefix (replace with your real KU ID for submission).

## Run
```bash
mvn -q clean package
mvn -q exec:java        # or: java -jar target/ecoride-console-uml-1.0.0-shaded.jar
mvn -q test
```

## Notes
- The **diagram** uses the K1234567_ prefix; the **code** uses normal Java names for readability.
- See `docs/ReportOutline.md` mapped to your marking scheme.
