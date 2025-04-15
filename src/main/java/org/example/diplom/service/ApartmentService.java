package org.example.diplom.service;

import org.example.diplom.model.Apartment;
import org.example.diplom.model.User;
import org.example.diplom.repository.ApartmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class ApartmentService {

    private final ApartmentRepository apartmentRepository;

    @Autowired
    public ApartmentService(ApartmentRepository apartmentRepository) {
        this.apartmentRepository = apartmentRepository;
    }

    public List<Apartment> getApartmentsByOwner(User owner) {
        if (owner == null) {
            return Collections.emptyList();
        }
        return apartmentRepository.findByOwner(owner);
    }
}