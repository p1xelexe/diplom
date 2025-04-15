package org.example.diplom.repository;

import org.example.diplom.model.Apartment;
import org.example.diplom.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ApartmentRepository extends JpaRepository<Apartment, Long> {
    List<Apartment> findByOwner(User owner);
}