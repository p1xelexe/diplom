package org.example.diplom.controller;

import org.example.diplom.model.Apartment;
import org.example.diplom.model.Meter;
import org.example.diplom.model.MeterReading;
import org.example.diplom.model.User;
import org.example.diplom.model.enums.MeterType;
import org.example.diplom.service.ApartmentService;
import org.example.diplom.service.MeterReadingService;
import org.example.diplom.service.MeterService;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Controller
public class MeterController {

    private final ApartmentService apartmentService;
    private final UserService userService;
    private final MeterService meterService;
    private final MeterReadingService meterReadingService;

    @Autowired
    public MeterController(ApartmentService apartmentService, UserService userService,
                           MeterService meterService, MeterReadingService meterReadingService) {
        this.apartmentService = apartmentService;
        this.userService = userService;
        this.meterService = meterService;
        this.meterReadingService = meterReadingService;
    }

    @GetMapping("/meters")
    public String showMeters(Model model, Authentication authentication) {
        try {
            if (authentication == null || !authentication.isAuthenticated()) {
                model.addAttribute("error", "Пользователь не аутентифицирован");
                model.addAttribute("apartments", Collections.emptyList());
                model.addAttribute("meters", Collections.emptyList());
                return "meters/list";
            }

            String username = authentication.getName();
            User currentUser = userService.getUserByEmail(username);

            if (currentUser == null) {
                currentUser = new User();
                currentUser.setEmail(username);
                currentUser.setRole("ROLE_RESIDENT");

                // Сохраняем пользователя, если его не существует
                try {
                    currentUser = userService.registerUser(currentUser);
                } catch (Exception ex) {
                    System.out.println("Не удалось сохранить пользователя: " + ex.getMessage());
                }

                model.addAttribute("error", "Пользователь не найден в базе данных, создан временный профиль");
            }

            List<Apartment> apartments = apartmentService.getApartmentsByOwner(currentUser);
            if (apartments == null || apartments.isEmpty()) {
                // Если у пользователя нет квартир, создаем тестовую квартиру
                if (currentUser.getId() != null) {
                    Apartment apartment = new Apartment();
                    apartment.setIdentifier("Квартира #1");
                    apartment.setAddress("ул. Тестовая, д. 1");
                    apartment.setOwner(currentUser);

                    // Здесь должен быть код для сохранения квартиры в базе данных
                    // apartmentService.saveApartment(apartment);

                    // Если нет метода для сохранения квартиры, можно только добавить в модель для отображения
                    model.addAttribute("info", "Создана тестовая квартира (только для отображения)");

                    apartments = new ArrayList<>();
                    apartments.add(apartment);

                    // Добавляем тестовый счетчик
                    Meter meter = new Meter();
                    meter.setSerialNumber("ТСЧ-12345");
                    meter.setType(MeterType.ELECTRICITY);
                    meter.setApartment(apartment);

                    List<Meter> meters = new ArrayList<>();
                    meters.add(meter);

                    model.addAttribute("meters", meters);
                } else {
                    apartments = Collections.emptyList();
                    model.addAttribute("error", "У вас нет зарегистрированных помещений");
                    model.addAttribute("meters", Collections.emptyList());
                }
            } else {
                // Получаем все счетчики для всех квартир пользователя
                List<Meter> allMeters = new ArrayList<>();
                for (Apartment apartment : apartments) {
                    Iterable<Meter> apartmentMeters = meterService.getMetersByApartment(apartment);
                    for (Meter meter : apartmentMeters) {
                        allMeters.add(meter);
                    }
                }

                if (allMeters.isEmpty()) {
                    // Если счетчиков нет, создаем тестовые для отображения
                    for (Apartment apartment : apartments) {
                        Meter meter = new Meter();
                        meter.setSerialNumber("ТСЧ-" + apartment.getId() + "-12345");
                        meter.setType(MeterType.ELECTRICITY);
                        meter.setApartment(apartment);
                        allMeters.add(meter);
                    }
                    model.addAttribute("info", "Созданы тестовые счетчики (только для отображения)");
                }

                model.addAttribute("meters", allMeters);
            }

            model.addAttribute("apartments", apartments);
            model.addAttribute("user", currentUser);
            model.addAttribute("meterTypes", Arrays.asList(MeterType.values()));
            return "meters/list";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Произошла ошибка при загрузке страницы: " + e.getMessage());
            model.addAttribute("apartments", Collections.emptyList());
            model.addAttribute("meters", Collections.emptyList());
            return "meters/list";
        }
    }

    @GetMapping("/meters/{id}/readings/add")
    public String showAddReadingForm(@PathVariable Long id, Model model, Authentication authentication) {
        try {
            if (authentication == null || !authentication.isAuthenticated()) {
                return "redirect:/login";
            }

            Optional<Meter> meterOpt = meterService.getMeterById(id);
            Meter meter;

            if (!meterOpt.isPresent()) {
                // Если счетчик не найден в БД, создаем временный для отображения формы
                meter = new Meter();
                meter.setId(id);
                meter.setSerialNumber("ТСЧ-" + id);
                meter.setType(MeterType.ELECTRICITY);

                String username = authentication.getName();
                User currentUser = userService.getUserByEmail(username);

                List<Apartment> apartments = apartmentService.getApartmentsByOwner(currentUser);
                if (apartments != null && !apartments.isEmpty()) {
                    meter.setApartment(apartments.get(0));
                } else {
                    // Создаем временную квартиру
                    Apartment apartment = new Apartment();
                    apartment.setIdentifier("Квартира #1");
                    apartment.setAddress("ул. Тестовая, д. 1");
                    meter.setApartment(apartment);
                }

                model.addAttribute("warning", "Счетчик не найден в базе данных, используется временный счетчик");
            } else {
                meter = meterOpt.get();
            }

            // Получаем последнее показание
            Optional<MeterReading> lastReading = meterReadingService.getLastReading(meter);

            // Создаем новое показание
            MeterReading newReading = new MeterReading();
            newReading.setMeter(meter);
            newReading.setDate(LocalDate.now());

            model.addAttribute("meter", meter);
            model.addAttribute("reading", newReading);
            if (lastReading.isPresent()) {
                model.addAttribute("lastReading", lastReading.get());
            }

            return "meters/add-reading";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Ошибка при подготовке формы: " + e.getMessage());
            return "redirect:/meters";
        }
    }

    @PostMapping("/meters/{id}/readings/add")
    public String addReading(@PathVariable Long id, @ModelAttribute MeterReading reading,
                             BindingResult result, Model model, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "meters/add-reading";
        }

        try {
            Meter meter = reading.getMeter();

            // Проверяем, существует ли счетчик в базе данных
            if (meter == null || meter.getId() == null) {
                Optional<Meter> meterOpt = meterService.getMeterById(id);
                if (meterOpt.isPresent()) {
                    reading.setMeter(meterOpt.get());
                } else {
                    throw new IllegalArgumentException("Счетчик не найден в базе данных");
                }
            }

            MeterReading savedReading = meterReadingService.saveReading(reading);
            redirectAttributes.addFlashAttribute("success", "Показания успешно сохранены");
            return "redirect:/meters";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Ошибка при сохранении показаний: " + e.getMessage());
            model.addAttribute("meter", reading.getMeter());

            // Получаем последнее показание для отображения
            Optional<MeterReading> lastReading = meterReadingService.getLastReading(reading.getMeter());
            if (lastReading.isPresent()) {
                model.addAttribute("lastReading", lastReading.get());
            }

            return "meters/add-reading";
        }
    }

    @GetMapping("/meters/readings/submit")
    public String showSubmitReadingForm(Model model, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }

        String username = authentication.getName();
        User currentUser = userService.getUserByEmail(username);

        List<Apartment> apartments = apartmentService.getApartmentsByOwner(currentUser);

        // Создаем пустое показание
        MeterReading reading = new MeterReading();
        reading.setDate(LocalDate.now());

        model.addAttribute("reading", reading);
        model.addAttribute("apartments", apartments);
        model.addAttribute("meterTypes", Arrays.asList(MeterType.values()));

        return "meters/submit-reading";
    }

    @PostMapping("/meters/readings/submit")
    public String submitReading(@ModelAttribute MeterReading reading,
                                @RequestParam Long apartmentId,
                                @RequestParam MeterType meterType,
                                BindingResult result, Model model, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "meters/submit-reading";
        }

        try {
            // Получаем квартиру
            Optional<Apartment> apartmentOpt = apartmentService.getApartmentById(apartmentId);
            if (!apartmentOpt.isPresent()) {
                throw new IllegalArgumentException("Квартира не найдена");
            }

            Apartment apartment = apartmentOpt.get();

            // Ищем счетчик нужного типа для квартиры
            Meter meter = null;
            Iterable<Meter> meters = meterService.getMetersByApartmentAndType(apartment, meterType);
            for (Meter m : meters) {
                meter = m;
                break;
            }

            // Если счетчик не найден, создаем новый
            if (meter == null) {
                meter = new Meter();
                meter.setType(meterType);
                meter.setSerialNumber("AUTO-" + apartment.getId() + "-" + meterType.name());
                meter.setApartment(apartment);
                meter = meterService.saveMeter(meter);
            }

            // Устанавливаем счетчик для показания
            reading.setMeter(meter);

            // Сохраняем показание
            meterReadingService.saveReading(reading);

            redirectAttributes.addFlashAttribute("success", "Показания успешно сохранены");
            return "redirect:/meters";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Ошибка при сохранении показаний: " + e.getMessage());

            // Получаем данные для формы
            String username = ((Authentication)model.getAttribute("authentication")).getName();
            User currentUser = userService.getUserByEmail(username);
            List<Apartment> apartments = apartmentService.getApartmentsByOwner(currentUser);

            model.addAttribute("apartments", apartments);
            model.addAttribute("meterTypes", Arrays.asList(MeterType.values()));

            return "meters/submit-reading";
        }
    }
}