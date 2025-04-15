package org.example.diplom.repository;

import org.example.diplom.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);
    Iterable<User> findAllByRole(String role);
}