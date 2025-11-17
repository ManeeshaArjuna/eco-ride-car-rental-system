package com.ecoride.repository;

import com.ecoride.domain.Customer;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class InMemoryCustomerRepository implements CustomerRepository {
    private final Map<String, Customer> data = new ConcurrentHashMap<>();

    @Override public void save(Customer c) { data.put(c.getCustomerId(), c); }
    @Override public Optional<Customer> findById(String id) { return Optional.ofNullable(data.get(id)); }
    @Override public List<Customer> findAll() { return new ArrayList<>(data.values()); }

    @Override public List<Customer> findByNameContains(String name) {
        String q = name.toLowerCase(Locale.ROOT);
        return data.values().stream()
                .filter(c -> c.getName() != null && c.getName().toLowerCase(Locale.ROOT).contains(q))
                .collect(Collectors.toList());
    }
}
