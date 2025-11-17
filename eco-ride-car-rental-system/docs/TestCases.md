# Test Cases (Manual)

| Test Case ID | Function Tested | Input | Expected Output |
|---|---|---|---|
| TC001 | Add New Vehicle | Type=ELECTRIC, ID=C-010 | Vehicle added |
| TC002 | Update Vehicle Status | ID=C-010, Status=UNDER_MAINTENANCE | Status updated |
| TC003 | Register Local Customer | NIC=991234567V, Name=Sam | Customer registered |
| TC004 | Book by Category | Customer=NIC1, Category=HYBRID, Start=today+3, Days=3, KM=300 | Booking ID displayed, deposit charged |
| TC005 | Reject Lead Time | Start=today+2 | Error: at least 3 days in advance |
| TC006 | Cancel Booking (allowed) | Cancel within 2 days | Cancelled & vehicle available |
| TC007 | Cancel Booking (rejected) | Cancel after 2 days | Error shown |
| TC008 | Update Booking (allowed) | Update km within 2 days | Updated |
| TC009 | Search Booking | Query by customer name or booking ID | Matching list shown |
| TC010 | Invoice Generation | Complete booking with 8 days, 850 km (Compact) | Final payable LKR 37,350 |