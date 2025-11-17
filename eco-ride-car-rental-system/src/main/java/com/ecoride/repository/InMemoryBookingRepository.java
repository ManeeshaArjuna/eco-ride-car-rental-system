package com.ecoride.repository;

import com.ecoride.domain.Booking;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class InMemoryBookingRepository implements BookingRepository {
    private final Map<String, Booking> data = new ConcurrentHashMap<>();

    @Override public void save(Booking b) { data.put(b.getBookingId(), b); }
    @Override public Optional<Booking> findById(String id) { return Optional.ofNullable(data.get(id)); }
    @Override public List<Booking> findAll() { return new ArrayList<>(data.values()); }
    @Override public void delete(String id) { data.remove(id); }

    @Override public List<Booking> findByDate(LocalDate date) {
        return data.values().stream()
                .filter(b -> b.getStartDate().equals(date))
                .collect(Collectors.toList());
    }
}
