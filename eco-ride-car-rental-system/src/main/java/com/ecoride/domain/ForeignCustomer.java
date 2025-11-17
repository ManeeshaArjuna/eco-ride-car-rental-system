package com.ecoride.domain;

public class ForeignCustomer extends Customer {
    private String passportNumber;
    private String nationality;

    public ForeignCustomer(String passportNumber, String nationality, String name, String contact, String email) {
        super(passportNumber, name, contact, email);
        this.passportNumber = passportNumber;
        this.nationality = nationality;
    }

    public String getPassportNumber() { return passportNumber; }
    public String getNationality() { return nationality; }
    public boolean verifyPassport() { return passportNumber != null && passportNumber.length() >= 6; }
}
