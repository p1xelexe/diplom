package org.example.diplom.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

/**
 * Класс, представляющий показания счетчика.
 * Согласно Главе 1, п. 1.1, и Главе 2, п. 2.3.6, показания счетчиков
 * являются основой для расчета потребления ресурсов и формирования платежей.
 */
@Entity
@Data
public class MeterReading {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "meter_id", nullable = false)
    private Meter meter; // Счетчик, к которому относятся показания

    @Column(nullable = false)
    private Double value; // Значение показаний

    @Column(nullable = false)
    private LocalDate date; // Дата снятия показаний

    // Расчет потребления на основе предыдущих показаний
    public Double calculateConsumption(Double previousReading) {
        return this.value - previousReading;
    }
}