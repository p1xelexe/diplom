package org.example.diplom.model;

import jakarta.persistence.*;
import lombok.Data;
import org.example.diplom.model.enums.MeterType;
import java.util.List;

/**
 * Класс, представляющий прибор учета (счетчик).
 * Согласно Главе 1, п. 1.1, в ЖКХ используются различные типы приборов учета
 * для контроля потребления ресурсов (вода, электричество и т.д.).
 */
@Entity
@Data
public class Meter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MeterType type; // Тип счетчика (вода, электричество и т.д.)

    @Column(nullable = false)
    private String serialNumber; // Серийный номер прибора учета

    @ManyToOne
    @JoinColumn(name = "apartment_id", nullable = false)
    private Apartment apartment; // Помещение, в котором установлен прибор

    @OneToMany(mappedBy = "meter", cascade = CascadeType.ALL)
    private List<MeterReading> readings; // История показаний
}