package org.example.diplom.model.enums;

/**
 * Перечисление статусов заявок на обслуживание.
 * Согласно Главе 2, п. 2.3.3, заявки имеют различные статусы
 * в процессе их обработки.
 */
public enum RequestStatus {
    NEW("Новая"),
    IN_PROGRESS("В работе"),
    COMPLETED("Выполнена"),
    CANCELED("Отменена");

    private final String displayName;

    RequestStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}