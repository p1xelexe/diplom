package org.example.diplom.model;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

/**
 * Класс, представляющий жилое помещение (квартиру).
 * Согласно Главе 1, п. 1.1, жилое помещение является базовой единицей
 * для учета коммунальных услуг и связано с жильцами и приборами учета.
 */
@Entity
@Data
public class Apartment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String identifier; // Номер квартиры или уникальный идентификатор

    private String address; // Адрес помещения

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User owner; // Собственник помещения

    @OneToMany(mappedBy = "apartment", cascade = CascadeType.ALL)
    private List<Meter> meters; // Список приборов учета в помещении

    @OneToMany(mappedBy = "apartment", cascade = CascadeType.ALL)
    private List<ServiceRequest> serviceRequests; // Заявки по данному помещению
}