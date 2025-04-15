package org.example.diplom.repository;

import org.example.diplom.model.Meter;
import org.example.diplom.model.enums.MeterType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Репозиторий для работы с сущностью Meter (прибор учета).
 * Согласно Главе 2, п. 2.5, репозитории обеспечивают доступ к данным
 * через Hibernate.
 */
@Repository
public interface MeterRepository extends JpaRepository<Meter, Long> {

    // Поиск счетчиков по идентификатору помещения
    Iterable<Meter> findAllByApartmentId(Long apartmentId);

    // Поиск счетчиков определенного типа в помещении
    Iterable<Meter> findAllByApartmentIdAndType(Long apartmentId, MeterType type);

    // Поиск счетчика по серийному номеру
    Meter findBySerialNumber(String serialNumber);
}