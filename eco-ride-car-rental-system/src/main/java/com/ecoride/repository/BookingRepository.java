package com.ecoride.repository;

import com.ecoride.domain.Booking;
import java.time.LocalDate;
import java.util.*;

public interface BookingRepository {
    void save(Booking b);
    Optional<Booking> findById(String id);
    List<Booking> findAll();
    List<Booking> findByDate(LocalDate date);
    void delete(String id);
}
