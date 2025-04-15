package org.example.diplom.service;

import org.example.diplom.model.Meter;
import org.example.diplom.model.MeterReading;
import org.example.diplom.repository.MeterReadingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Сервис для работы с показаниями счетчиков.
 * Согласно Главе 2, п. 2.5, сервисы содержат бизнес-логику приложения.
 */
@Service
public class MeterReadingService {

    private final MeterReadingRepository meterReadingRepository;

    @Autowired
    public MeterReadingService(MeterReadingRepository meterReadingRepository) {
        this.meterReadingRepository = meterReadingRepository;
    }

    /**
     * Получение всех показаний
     */
    public List<MeterReading> getAllReadings() {
        return meterReadingRepository.findAll();
    }

    /**
     * Получение показания по ID
     */
    public Optional<MeterReading> getReadingById(Long id) {
        return meterReadingRepository.findById(id);
    }

    /**
     * Получение показаний по счетчику
     */
    public Iterable<MeterReading> getReadingsByMeter(Meter meter) {
        return meterReadingRepository.findAllByMeterId(meter.getId());
    }

    /**
     * Получение показаний за период
     */
    public Iterable<MeterReading> getReadingsByMeterAndPeriod(Meter meter, LocalDate startDate, LocalDate endDate) {
        return meterReadingRepository.findAllByMeterIdAndDateBetween(meter.getId(), startDate, endDate);
    }

    /**
     * Получение последнего показания счетчика
     */
    public Optional<MeterReading> getLastReading(Meter meter) {
        return meterReadingRepository.findLastReadingByMeter(meter);
    }

    /**
     * Сохранение показания
     */
    public MeterReading saveReading(MeterReading reading) {
        // Валидация показаний (новые должны быть больше предыдущих)
        Optional<MeterReading> lastReading = getLastReading(reading.getMeter());
        if (lastReading.isPresent() && reading.getValue() < lastReading.get().getValue()) {
            throw new IllegalArgumentException("Новые показания не могут быть меньше предыдущих");
        }

        return meterReadingRepository.save(reading);
    }

    /**
     * Расчет потребления за период
     */
    public Double calculateConsumption(Meter meter, LocalDate startDate, LocalDate endDate) {
        Iterable<MeterReading> readings = getReadingsByMeterAndPeriod(meter, startDate, endDate);

        // Находим минимальное и максимальное показание за период
        Double minReading = Double.MAX_VALUE;
        Double maxReading = Double.MIN_VALUE;

        for (MeterReading reading : readings) {
            if (reading.getValue() < minReading) {
                minReading = reading.getValue();
            }
            if (reading.getValue() > maxReading) {
                maxReading = reading.getValue();
            }
        }

        // Если нет показаний за период, возвращаем 0
        if (minReading == Double.MAX_VALUE || maxReading == Double.MIN_VALUE) {
            return 0.0;
        }

        // Возвращаем разницу между максимальным и минимальным показанием
        return maxReading - minReading;
    }
}