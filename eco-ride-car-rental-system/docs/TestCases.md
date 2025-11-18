# EcoRide Car Rental System – Test Cases (Updated)

## VEHICLE MANAGEMENT
| TC ID | Description | Input | Expected Output |
|------|-------------|--------|------------------|
| TC001 | Add vehicle (Hybrid) – valid | Type=1, Model=“Aqua”, Battery=6.5, Efficiency=25 | ✔ Vehicle added (C-006) |
| TC002 | Add vehicle – invalid numeric | Battery=“abc” | ❌ “Invalid number” |
| TC003 | Add vehicle – invalid type | Type=9 | ❌ Invalid type |
| TC004 | Update status | ID=C-001, Status=UNDER_MAINTENANCE | ✔ Updated |
| TC005 | Remove vehicle | ID=C-003 | ✔ Removed |
| TC006 | Invalid vehicle ID | C1 | ❌ Invalid format |

## CUSTOMER REGISTRATION
| TC ID | Description | Input | Expected Output |
|------|-------------|--------|------------------|
| TC010 | Register Local Customer | Valid inputs | ✔ Registered |
| TC011 | Invalid name | “J0hn” | ❌ Error |
| TC012 | Invalid contact | “0771ABCD” | ❌ Error |
| TC013 | Invalid email | “abc123” | ❌ Error |
| TC014 | Register Foreign | Passport + Nationality valid | ✔ Registered |

## ADMIN LOGIN
| TC ID | Description | Input | Expected Output |
|------|-------------|--------|------------------|
| TC020 | Valid login | admin/admin123 | ✔ Allowed |
| TC021 | Wrong password | admin/wrong | ❌ Error |
| TC022 | Blank | "" | ❌ Error |

## BOOKING
| TC ID | Description | Input | Expected Output |
|------|-------------|--------|------------------|
| TC030 | Book by vehicle ID | C-001 + valid data | ✔ Booking created |
| TC031 | Book by category | HYBRID | ✔ Vehicle assigned |
| TC032 | Start < 3 days | today+2 | ❌ Error |
| TC033 | Invalid KM | “xyz” | ❌ Error |
| TC034 | Invalid vehicle ID | C-99X | ❌ Invalid format |

## UPDATE BOOKING
| TC ID | Description | Input | Expected Output |
|------|-------------|--------|------------------|
| TC040 | Update within 2 days | Valid booking | ✔ Updated |
| TC041 | Update after 2 days | Old booking | ❌ Not allowed |
| TC042 | Invalid new date | “2025-13-40” | ❌ Error |
| TC043 | New start < 3 days | today+1 | ❌ Error |
| TC044 | Invalid booking ID | R-123 | ❌ Invalid |

## CANCEL BOOKING
| TC ID | Description | Input | Expected Output |
|------|-------------|--------|------------------|
| TC050 | Cancel valid | Within 2 days | ✔ Cancelled |
| TC051 | Cancel late | After 2 days | ❌ Error |
| TC052 | ID not found | R-ffffffff | ❌ Not found |

## SEARCH & VIEW
| TC ID | Description | Expected Output |
|------|-------------|------------------|
| TC060 | Search by ID | Shows booking |
| TC061 | Search by name | Shows matches |
| TC062 | Invalid search | “No results” |
| TC063 | View by date | Bookings shown |
| TC064 | Invalid date | ❌ Error |

## COMPLETE BOOKING + INVOICE
| TC ID | Description | Expected Output |
|------|-------------|------------------|
| TC070 | Complete booking | Invoice printed |
| TC071 | Complete cancelled booking | ❌ Error |
| TC072 | Complete completed booking | ❌ Error |
| TC073 | Invoice value check | Matches calculation |

## VALIDATION (ID formats)
| TC ID | Description | Expected Output |
|------|-------------|------------------|
| TC080 | Valid vehicle ID | Accepted |
| TC081 | Invalid vehicle ID | ❌ Invalid |
| TC082 | Valid booking ID | Accepted |
| TC083 | Invalid booking ID | ❌ Invalid |

## NAVIGATION
| TC ID | Description | Expected Output |
|------|-------------|------------------|
| TC090 | Press # | Returns to menu |
| TC091 | # in admin login | Returns safely |
| TC092 | # during booking | Cancels safely |

## TABLE FORMATTING & UI
| TC ID | Description | Expected Output |
|------|-------------|------------------|
| TC100 | Vehicle list formatting | Proper table |
| TC101 | Available vehicles list | Matches formatting |
