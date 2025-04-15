package org.example.diplom.model.enums;

/**
 * Перечисление типов приборов учета.
 * Согласно Главе 1, п. 1.1, в ЖКХ используются различные типы приборов
 * для учета потребления разных ресурсов.
 */
public enum MeterType {
    WATER_HOT("Горячая вода"),
    WATER_COLD("Холодная вода"),
    ELECTRICITY("Электричество"),
    GAS("Газ"),
    HEATING("Отопление");

    private final String displayName;

    MeterType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}