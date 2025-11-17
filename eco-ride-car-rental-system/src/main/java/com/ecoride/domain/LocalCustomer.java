package com.ecoride.domain;

public class LocalCustomer extends Customer {
    private String nicNumber;

    public LocalCustomer(String nicNumber, String name, String contact, String email) {
        super(nicNumber, name, contact, email);
        this.nicNumber = nicNumber;
    }

    public String getNicNumber() { return nicNumber; }
    public boolean verifyNIC() { return nicNumber != null && nicNumber.matches("[0-9VvXx]{10,12}"); }
}
