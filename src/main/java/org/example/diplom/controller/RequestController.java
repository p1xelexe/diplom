package org.example.diplom.controller;

import org.example.diplom.model.Apartment;
import org.example.diplom.model.User;
import org.example.diplom.service.ApartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Collections;
import java.util.List;

@Controller
public class RequestController {

    private final ApartmentService apartmentService;

    @Autowired
    public RequestController(ApartmentService apartmentService) {
        this.apartmentService = apartmentService;
    }

    @GetMapping("/requests")
    public String showRequests(Model model, Authentication authentication) {
        try {
            if (authentication == null || !authentication.isAuthenticated()) {
                System.out.println("Аутентификация отсутствует или пользователь не аутентифицирован");
                model.addAttribute("error", "Пользователь не аутентифицирован");
                return "requests";
            }

            String username = authentication.getName();
            System.out.println("Аутентифицирован пользователь: " + username);

            User currentUser = new User();
            currentUser.setEmail(username);
            currentUser.setRole("ROLE_RESIDENT");

            List<Apartment> apartments = apartmentService.getApartmentsByOwner(currentUser);
            if (apartments == null) {
                System.out.println("Список квартир null для пользователя: " + username);
                apartments = Collections.emptyList();
            }

            model.addAttribute("apartments", apartments);
            model.addAttribute("user", currentUser);
            return "requests";
        } catch (Exception e) {
            System.out.println("Ошибка в RequestController: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("error", "Произошла ошибка при загрузке страницы");
            return "requests";
        }
    }
}