package org.example.diplom.service;

import org.example.diplom.model.Apartment;
import org.example.diplom.model.Meter;
import org.example.diplom.model.enums.MeterType;
import org.example.diplom.repository.MeterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Сервис для работы с приборами учета (счетчиками).
 * Согласно Главе 2, п. 2.5, сервисы содержат бизнес-логику приложения.
 */
@Service
public class MeterService {

    private final MeterRepository meterRepository;

    @Autowired
    public MeterService(MeterRepository meterRepository) {
        this.meterRepository = meterRepository;
    }

    /**
     * Получение всех счетчиков
     */
    public List<Meter> getAllMeters() {
        return meterRepository.findAll();
    }

    /**
     * Получение счетчика по ID
     */
    public Optional<Meter> getMeterById(Long id) {
        return meterRepository.findById(id);
    }

    /**
     * Получение счетчиков по помещению
     */
    public Iterable<Meter> getMetersByApartment(Apartment apartment) {
        return meterRepository.findAllByApartmentId(apartment.getId());
    }

    /**
     * Получение счетчиков определенного типа по помещению
     */
    public Iterable<Meter> getMetersByApartmentAndType(Apartment apartment, MeterType type) {
        return meterRepository.findAllByApartmentIdAndType(apartment.getId(), type);
    }

    /**
     * Получение счетчика по серийному номеру
     */
    public Meter getMeterBySerialNumber(String serialNumber) {
        return meterRepository.findBySerialNumber(serialNumber);
    }

    /**
     * Сохранение счетчика
     */
    public Meter saveMeter(Meter meter) {
        return meterRepository.save(meter);
    }

    /**
     * Удаление счетчика
     */
    public void deleteMeter(Long id) {
        meterRepository.deleteById(id);
    }
}