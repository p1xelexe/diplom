package org.example.diplom.repository;

import org.example.diplom.model.Meter;
import org.example.diplom.model.MeterReading;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

/**
 * Репозиторий для работы с сущностью MeterReading (показания счетчика).
 * Согласно Главе 2, п. 2.5, репозитории обеспечивают доступ к данным
 * через Hibernate.
 */
@Repository
public interface MeterReadingRepository extends JpaRepository<MeterReading, Long> {

    // Поиск показаний по идентификатору счетчика
    Iterable<MeterReading> findAllByMeterId(Long meterId);

    // Поиск показаний за определенный период
    Iterable<MeterReading> findAllByMeterIdAndDateBetween(Long meterId, LocalDate startDate, LocalDate endDate);

    // Получение последнего показания счетчика
    @Query("SELECT mr FROM MeterReading mr WHERE mr.meter = ?1 ORDER BY mr.date DESC")
    Optional<MeterReading> findLastReadingByMeter(Meter meter);
}