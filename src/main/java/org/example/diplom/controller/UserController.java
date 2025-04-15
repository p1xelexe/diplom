package org.example.diplom.controller;

import org.example.diplom.model.Apartment;
import org.example.diplom.model.User;
import org.example.diplom.service.ApartmentService;
import org.example.diplom.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class UserController {

    private final UserService userService;
    private final ApartmentService apartmentService;

    @Autowired
    public UserController(UserService userService, ApartmentService apartmentService) {
        this.userService = userService;
        this.apartmentService = apartmentService;
    }

    @GetMapping("/")
    public String home(Model model, Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            return "redirect:/dashboard";
        }
        return "home";
    }

    @GetMapping("/login")
    public String showLoginForm() {
        return "login";
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute User user, BindingResult result) {
        if (result.hasErrors()) {
            return "register";
        }
        try {
            user.setRole("ROLE_RESIDENT");
            userService.registerUser(user);
            return "redirect:/login?registered";
        } catch (IllegalArgumentException e) {
            result.rejectValue("email", "error.user", e.getMessage());
            return "register";
        }
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model, Authentication authentication) {
        String username = authentication.getName();
        User currentUser = userService.getUserByEmail(username);
        if (currentUser == null) {
            // Если пользователь не найден в базе, создаём временного
            currentUser = new User();
            currentUser.setEmail(username);
            currentUser.setRole("ROLE_RESIDENT");
        }
        model.addAttribute("user", currentUser);
        List<Apartment> apartments = apartmentService.getApartmentsByOwner(currentUser);
        model.addAttribute("apartments", apartments);
        return "dashboard";
    }

    @GetMapping("/profile")
    public String showProfile(Model model, Authentication authentication) {
        String username = authentication.getName();
        User currentUser = userService.getUserByEmail(username);
        if (currentUser == null) {
            currentUser = new User();
            currentUser.setEmail(username);
            currentUser.setRole("ROLE_RESIDENT");
        }
        model.addAttribute("user", currentUser);
        return "profile";
    }

    @PostMapping("/profile")
    public String updateProfile(@ModelAttribute User user, BindingResult result) {
        if (result.hasErrors()) {
            return "profile";
        }
        userService.updateUser(user);
        return "redirect:/profile?updated";
    }

    @GetMapping("/change-password")
    public String showChangePasswordForm() {
        return "change-password";
    }

    @PostMapping("/change-password")
    public String changePassword(@RequestParam String oldPassword,
                                 @RequestParam String newPassword,
                                 Authentication authentication) {
        String username = authentication.getName();
        User currentUser = userService.getUserByEmail(username);
        if (currentUser == null) {
            currentUser = new User();
            currentUser.setEmail(username);
            currentUser.setRole("ROLE_RESIDENT");
        }
        try {
            userService.changePassword(currentUser.getId(), oldPassword, newPassword);
            return "redirect:/profile?passwordChanged";
        } catch (IllegalArgumentException e) {
            return "redirect:/change-password?error";
        }
    }

    @GetMapping("/admin/users")
    public String listUsers(Model model) {
        List<User> users = userService.getAllUsers();
        model.addAttribute("users", users);
        return "admin/users";
    }

    @GetMapping("/admin/users/{id}/edit")
    public String showEditUserForm(@PathVariable Long id, Model model) {
        java.util.Optional<User> user = userService.getUserById(id);
        if (user.isPresent()) {
            model.addAttribute("user", user.get());
            return "admin/edit-user";
        } else {
            return "redirect:/admin/users";
        }
    }

    @PostMapping("/admin/users/{id}/edit")
    public String editUser(@PathVariable Long id, @ModelAttribute User user, BindingResult result) {
        if (result.hasErrors()) {
            return "admin/edit-user";
        }
        userService.updateUser(user);
        return "redirect:/admin/users";
    }

    @PostMapping("/admin/users/{id}/delete")
    public String deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return "redirect:/admin/users";
    }
}