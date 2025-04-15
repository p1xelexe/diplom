package org.example.diplom.service;

import org.example.diplom.model.Apartment;
import org.example.diplom.model.User;
import org.example.diplom.repository.ApartmentRepository;
import org.example.diplom.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class ApartmentService {

    private final ApartmentRepository apartmentRepository;
    private final UserRepository userRepository;

    @Autowired
    public ApartmentService(ApartmentRepository apartmentRepository, UserRepository userRepository) {
        this.apartmentRepository = apartmentRepository;
        this.userRepository = userRepository;
    }

    public List<Apartment> getApartmentsByOwner(User owner) {
        if (owner == null) {
            return Collections.emptyList();
        }

        // Если пользователь уже существует в базе, используем его
        // Иначе, пытаемся найти пользователя по email
        User persistentUser = null;
        if (owner.getId() != null) {
            persistentUser = owner;
        } else {
            persistentUser = userRepository.findByEmail(owner.getEmail());
        }

        // Если пользователя нет в базе, возвращаем пустой список
        if (persistentUser == null) {
            return Collections.emptyList();
        }

        return apartmentRepository.findByOwner(persistentUser);
    }

    public Optional<Apartment> getApartmentById(Long id) {
        return apartmentRepository.findById(id);
    }

    public Apartment saveApartment(Apartment apartment) {
        return apartmentRepository.save(apartment);
    }
}