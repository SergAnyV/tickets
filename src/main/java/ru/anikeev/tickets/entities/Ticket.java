package ru.anikeev.tickets.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.math.BigDecimal;

/**
 * Класс, представляющий авиабилет.
 * Содержит информацию о рейсе, включая пункты отправления/назначения,
 * даты/время вылета и прилета, авиакомпанию и стоимость.
 * Аннотации @JsonProperty используются для маппинга полей при парсинге JSON.
 */
@Data
public class Ticket {
    /**
     * Код аэропорта отправления (например, "VVO" для Владивостока)
     */
    @JsonProperty("origin")
    private String origin;
    /**
     * Название аэропорта отправления
     */
    @JsonProperty("origin_name")
    private String originName;
    /**
     * Код аэропорта назначения (например, "TLV" для Тель-Авива)
     */
    @JsonProperty("destination")
    private String destination;
    /**
     * Название аэропорта назначения
     */
    @JsonProperty("destination_name")
    private String destinationName;
    /**
     * Дата вылета в формате, указанном в настройках приложения
     */
    @JsonProperty("departure_date")
    private String departureDate;
    /**
     * Время вылета в формате, указанном в настройках приложения
     */
    @JsonProperty("departure_time")
    private String departureTime;
    /**
     * Дата прилета в формате, указанном в настройках приложения
     */
    @JsonProperty("arrival_date")
    private String arrivalDate;
    /**
     * Время прилета в формате, указанном в настройках приложения
     */
    @JsonProperty("arrival_time")
    private String arrivalTime;
    /**
     * Код авиакомпании (например, "SU" для Аэрофлота)
     */
    @JsonProperty("carrier")
    private String carrier;
    /**
     * Количество пересадок (0 для прямого рейса)
     */
    @JsonProperty("stops")
    private int stops;
    /**
     * Стоимость билета
     */
    @JsonProperty("price")
    private BigDecimal price;
}
