package org.example.diplom.repository;

import org.example.diplom.model.ServiceRequest;
import org.example.diplom.model.enums.RequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

/**
 * Репозиторий для работы с сущностью ServiceRequest (заявка на обслуживание).
 * Согласно Главе 2, п. 2.5, репозитории обеспечивают доступ к данным
 * через Hibernate.
 */
@Repository
public interface ServiceRequestRepository extends JpaRepository<ServiceRequest, Long> {

    // Поиск заявок по идентификатору помещения
    Iterable<ServiceRequest> findAllByApartmentId(Long apartmentId);

    // Поиск заявок по статусу
    Iterable<ServiceRequest> findAllByStatus(RequestStatus status);

    // Поиск заявок по помещению и статусу
    Iterable<ServiceRequest> findAllByApartmentIdAndStatus(Long apartmentId, RequestStatus status);

    // Поиск заявок, созданных в определенный период
    Iterable<ServiceRequest> findAllByCreatedAtBetween(LocalDateTime startDateTime, LocalDateTime endDateTime);
}