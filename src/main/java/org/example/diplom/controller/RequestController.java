package org.example.diplom.controller;

import org.example.diplom.model.Apartment;
import org.example.diplom.model.ServiceRequest;
import org.example.diplom.model.User;
import org.example.diplom.model.enums.RequestStatus;
import org.example.diplom.service.ApartmentService;
import org.example.diplom.service.ServiceRequestService;
import org.example.diplom.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Controller
public class RequestController {

    private final ApartmentService apartmentService;
    private final UserService userService;
    private final ServiceRequestService serviceRequestService;

    @Autowired
    public RequestController(ApartmentService apartmentService, UserService userService, ServiceRequestService serviceRequestService) {
        this.apartmentService = apartmentService;
        this.userService = userService;
        this.serviceRequestService = serviceRequestService;
    }

    @GetMapping("/requests")
    public String showRequests(Model model, Authentication authentication, @RequestParam(required = false) RequestStatus status) {
        try {
            if (authentication == null || !authentication.isAuthenticated()) {
                model.addAttribute("error", "Пользователь не аутентифицирован");
                model.addAttribute("apartments", Collections.emptyList());
                model.addAttribute("requests", Collections.emptyList());
                model.addAttribute("statuses", Arrays.asList(RequestStatus.values()));
                return "requests/list";
            }

            String username = authentication.getName();
            User currentUser = userService.getUserByEmail(username);

            if (currentUser == null) {
                currentUser = new User();
                currentUser.setEmail(username);
                currentUser.setRole("ROLE_RESIDENT");
                model.addAttribute("error", "Пользователь не найден в базе данных");
                model.addAttribute("apartments", Collections.emptyList());
                model.addAttribute("requests", Collections.emptyList());
                model.addAttribute("statuses", Arrays.asList(RequestStatus.values()));
                model.addAttribute("user", currentUser);
                return "requests/list";
            }

            List<Apartment> apartments = apartmentService.getApartmentsByOwner(currentUser);
            if (apartments == null) {
                apartments = Collections.emptyList();
            }

            // Получаем все заявки пользователя
            List<ServiceRequest> allRequests = new ArrayList<>();
            if (!apartments.isEmpty()) {
                // Получаем заявки для всех квартир пользователя
                for (Apartment apartment : apartments) {
                    if (status != null) {
                        // Если указан статус, фильтруем по нему
                        Iterable<ServiceRequest> apartmentRequests = serviceRequestService.getRequestsByApartmentAndStatus(apartment, status);
                        for (ServiceRequest req : apartmentRequests) {
                            allRequests.add(req);
                        }
                    } else {
                        // Иначе берем все заявки
                        Iterable<ServiceRequest> apartmentRequests = serviceRequestService.getRequestsByApartment(apartment);
                        for (ServiceRequest req : apartmentRequests) {
                            allRequests.add(req);
                        }
                    }
                }
            }

            model.addAttribute("apartments", apartments);
            model.addAttribute("user", currentUser);
            model.addAttribute("requests", allRequests);
            model.addAttribute("statuses", Arrays.asList(RequestStatus.values()));
            model.addAttribute("selectedStatus", status);
            return "requests/list";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Произошла ошибка при загрузке страницы: " + e.getMessage());
            model.addAttribute("apartments", Collections.emptyList());
            model.addAttribute("requests", Collections.emptyList());
            model.addAttribute("statuses", Arrays.asList(RequestStatus.values()));
            return "requests/list";
        }
    }

    @GetMapping("/requests/create")
    public String showCreateRequestForm(Model model, Authentication authentication) {
        try {
            if (authentication == null || !authentication.isAuthenticated()) {
                return "redirect:/login";
            }

            String username = authentication.getName();
            User currentUser = userService.getUserByEmail(username);

            if (currentUser == null) {
                model.addAttribute("error", "Пользователь не найден в базе данных");
                return "redirect:/dashboard";
            }

            List<Apartment> apartments = apartmentService.getApartmentsByOwner(currentUser);

            if (apartments == null || apartments.isEmpty()) {
                model.addAttribute("error", "У вас нет зарегистрированных помещений для создания заявки");
                return "redirect:/requests";
            }

            // Создаем пустую заявку и устанавливаем первую квартиру из списка
            ServiceRequest newRequest = new ServiceRequest();
            newRequest.setApartment(apartments.get(0));

            model.addAttribute("request", newRequest);
            model.addAttribute("apartments", apartments);
            return "requests/create";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Ошибка при подготовке формы создания заявки: " + e.getMessage());
            return "redirect:/requests";
        }
    }

    @PostMapping("/requests/create")
    public String createRequest(@ModelAttribute ServiceRequest request, BindingResult result, Model model, Authentication authentication) {
        if (result.hasErrors()) {
            return "requests/create";
        }

        try {
            // Сохраняем заявку
            ServiceRequest savedRequest = serviceRequestService.createRequest(request);
            return "redirect:/requests";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Ошибка при создании заявки: " + e.getMessage());

            // Получаем список квартир для формы
            String username = authentication.getName();
            User currentUser = userService.getUserByEmail(username);
            List<Apartment> apartments = apartmentService.getApartmentsByOwner(currentUser);
            model.addAttribute("apartments", apartments != null ? apartments : Collections.emptyList());

            return "requests/create";
        }
    }

    @GetMapping("/requests/{id}")
    public String viewRequest(@PathVariable Long id, Model model, Authentication authentication) {
        try {
            if (authentication == null || !authentication.isAuthenticated()) {
                return "redirect:/login";
            }

            String username = authentication.getName();
            User currentUser = userService.getUserByEmail(username);

            if (currentUser == null) {
                model.addAttribute("error", "Пользователь не найден в базе данных");
                return "redirect:/dashboard";
            }

            // Получаем заявку по ID
            java.util.Optional<ServiceRequest> requestOpt = serviceRequestService.getRequestById(id);
            if (!requestOpt.isPresent()) {
                model.addAttribute("error", "Заявка не найдена");
                return "redirect:/requests";
            }

            ServiceRequest request = requestOpt.get();

            // Проверка доступа (только для владельца квартиры или сотрудника)
            List<Apartment> userApartments = apartmentService.getApartmentsByOwner(currentUser);
            boolean hasAccess = currentUser.getRole().equals("ROLE_ADMIN") ||
                    currentUser.getRole().equals("ROLE_EMPLOYEE") ||
                    (userApartments != null && userApartments.contains(request.getApartment()));

            if (!hasAccess) {
                model.addAttribute("error", "У вас нет доступа к этой заявке");
                return "redirect:/requests";
            }

            model.addAttribute("request", request);
            model.addAttribute("statuses", Arrays.asList(RequestStatus.values()));
            return "requests/view";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Ошибка при просмотре заявки: " + e.getMessage());
            return "redirect:/requests";
        }
    }

    @PostMapping("/requests/{id}/update-status")
    public String updateRequestStatus(@PathVariable Long id, @RequestParam RequestStatus status,
                                      @RequestParam(required = false) String comment, Model model) {
        try {
            serviceRequestService.updateRequestStatus(id, status, comment);
            return "redirect:/requests/" + id + "?updated";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Ошибка при обновлении статуса: " + e.getMessage());
            return "redirect:/requests/" + id + "?error";
        }
    }

    @GetMapping("/requests/filter")
    public String filterRequests(@RequestParam(required = false) RequestStatus status) {
        return "redirect:/requests" + (status != null ? "?status=" + status : "");
    }
}