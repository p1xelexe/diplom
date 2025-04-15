package org.example.diplom.service;

import org.example.diplom.model.User;
import org.example.diplom.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public Iterable<User> getUsersByRole(String role) {
        return userRepository.findAllByRole(role);
    }

    public User registerUser(User user) {
        if (userRepository.findByEmail(user.getEmail()) != null) {
            throw new IllegalArgumentException("Пользователь с email " + user.getEmail() + " уже существует");
        }
        return userRepository.save(user);
    }

    public User updateUser(User user) {
        Optional<User> existingUser = userRepository.findById(user.getId());
        if (existingUser.isEmpty()) {
            throw new IllegalArgumentException("Пользователь с ID " + user.getId() + " не найден");
        }
        return userRepository.save(user);
    }

    public User changePassword(Long userId, String oldPassword, String newPassword) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (!oldPassword.equals(user.getPassword())) {
                throw new IllegalArgumentException("Неверный старый пароль");
            }
            user.setPassword(newPassword);
            return userRepository.save(user);
        } else {
            throw new IllegalArgumentException("Пользователь с ID " + userId + " не найден");
        }
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}