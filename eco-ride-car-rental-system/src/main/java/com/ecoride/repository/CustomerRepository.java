package com.ecoride.repository;

import com.ecoride.domain.Customer;

import java.util.*;

public interface CustomerRepository {
    void save(Customer c);
    Optional<Customer> findById(String id);
    List<Customer> findAll();
    List<Customer> findByNameContains(String name);
}
