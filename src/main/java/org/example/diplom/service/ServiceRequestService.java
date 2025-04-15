package org.example.diplom.service;

import org.example.diplom.model.Apartment;
import org.example.diplom.model.ServiceRequest;
import org.example.diplom.model.enums.RequestStatus;
import org.example.diplom.repository.ServiceRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Сервис для работы с заявками на обслуживание.
 * Согласно Главе 2, п. 2.5, сервисы содержат бизнес-логику приложения.
 */
@Service
public class ServiceRequestService {

    private final ServiceRequestRepository serviceRequestRepository;

    @Autowired
    public ServiceRequestService(ServiceRequestRepository serviceRequestRepository) {
        this.serviceRequestRepository = serviceRequestRepository;
    }

    /**
     * Получение всех заявок
     */
    public List<ServiceRequest> getAllRequests() {
        return serviceRequestRepository.findAll();
    }

    /**
     * Получение заявки по ID
     */
    public Optional<ServiceRequest> getRequestById(Long id) {
        return serviceRequestRepository.findById(id);
    }

    /**
     * Получение заявок по помещению
     */
    public Iterable<ServiceRequest> getRequestsByApartment(Apartment apartment) {
        return serviceRequestRepository.findAllByApartmentId(apartment.getId());
    }

    /**
     * Получение заявок по статусу
     */
    public Iterable<ServiceRequest> getRequestsByStatus(RequestStatus status) {
        return serviceRequestRepository.findAllByStatus(status);
    }

    /**
     * Получение заявок по помещению и статусу
     */
    public Iterable<ServiceRequest> getRequestsByApartmentAndStatus(Apartment apartment, RequestStatus status) {
        return serviceRequestRepository.findAllByApartmentIdAndStatus(apartment.getId(), status);
    }

    /**
     * Получение заявок, созданных в определенный период
     */
    public Iterable<ServiceRequest> getRequestsByCreationPeriod(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        return serviceRequestRepository.findAllByCreatedAtBetween(startDateTime, endDateTime);
    }

    /**
     * Создание новой заявки
     */
    public ServiceRequest createRequest(ServiceRequest request) {
        // Установка начальных значений
        request.setStatus(RequestStatus.NEW);
        request.setCreatedAt(LocalDateTime.now());

        return serviceRequestRepository.save(request);
    }

    /**
     * Обновление статуса заявки
     */
    public ServiceRequest updateRequestStatus(Long requestId, RequestStatus newStatus, String comment) {
        Optional<ServiceRequest> requestOpt = serviceRequestRepository.findById(requestId);

        if (requestOpt.isPresent()) {
            ServiceRequest request = requestOpt.get();
            request.setStatus(newStatus);
            request.setComment(comment);

            // Если заявка выполнена, устанавливаем время выполнения
            if (newStatus == RequestStatus.COMPLETED) {
                request.setCompletedAt(LocalDateTime.now());
            }

            return serviceRequestRepository.save(request);
        } else {
            throw new IllegalArgumentException("Заявка с ID " + requestId + " не найдена");
        }
    }
}