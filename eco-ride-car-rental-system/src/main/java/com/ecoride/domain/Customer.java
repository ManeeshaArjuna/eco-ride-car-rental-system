package com.ecoride.domain;

public abstract class Customer {
    protected String customerId; // NIC or Passport
    protected String name;
    protected String contact;
    protected String email;

    protected Customer(String customerId, String name, String contact, String email) {
        this.customerId = customerId;
        this.name = name;
        this.contact = contact;
        this.email = email;
    }

    public String getCustomerId() { return customerId; }
    public String getName() { return name; }
    public String getContact() { return contact; }
    public String getEmail() { return email; }

    public void setName(String name) { this.name = name; }
    public void setContact(String contact) { this.contact = contact; }
    public void setEmail(String email) { this.email = email; }

    @Override public String toString() {
        return customerId + " | " + name + " | " + contact + " | " + email;
    }
}
