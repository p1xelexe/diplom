package org.example.diplom.model;

import jakarta.persistence.*;
import lombok.Data;
import org.example.diplom.model.enums.RequestStatus;
import java.time.LocalDateTime;

/**
 * Класс, представляющий заявку на обслуживание.
 * Согласно Главе 1, п. 1.1, и Главе 2, п. 2.3.3, заявки являются
 * важным механизмом взаимодействия между жильцами и УК для решения
 * проблем с коммунальными услугами.
 */
@Entity
@Data
public class ServiceRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "apartment_id", nullable = false)
    private Apartment apartment; // Помещение, к которому относится заявка

    @Column(nullable = false, length = 500)
    private String description; // Описание проблемы

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RequestStatus status; // Статус заявки (новая, в работе, выполнена)

    @Column(nullable = false)
    private LocalDateTime createdAt; // Дата и время создания заявки

    private LocalDateTime completedAt; // Дата и время выполнения заявки

    private String comment; // Комментарий исполнителя
}