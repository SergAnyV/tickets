package ru.anikeev.tickets.repositories.imp;


import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import ru.anikeev.tickets.entities.Ticket;
import ru.anikeev.tickets.repositories.JsonReader;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
/**
 * Реализация интерфейса JsonReader для чтения и парсинга JSON-файлов с билетами.
 * Класс выполняет:
 * - Загрузку данных из JSON-файла
 * - Валидацию структуры файла и данных билетов
 * - Преобразование JSON в объекты Ticket
 */

@Repository
@Slf4j
public class JsonReaderImpl implements JsonReader {
    private final ObjectMapper mapper;
    private final String nameOfJsonStartField;
    private final String dateFormat;
    private final DateTimeFormatter timeFormatter;


    public JsonReaderImpl(
            @Value("${name.of.json.start.field}") String nameOfJsonStartField,
            @Value("${date.format}") String dateFormat,
            @Qualifier("timeFormatter") DateTimeFormatter timeFormatter,
            ObjectMapper mapper) {
        this.mapper = mapper;
        this.nameOfJsonStartField = nameOfJsonStartField;
        this.dateFormat = dateFormat;
        this.timeFormatter = timeFormatter;

    }

    /**
     * Читает JSON-файл по указанному пути и возвращает список билетов
     *
     * @param path путь к JSON-файлу с билетами
     * @return список объектов Ticket или пустой список при ошибках
     */
    @Override
    public List<Ticket> readJsonByPath(String path) {
        JsonFactory factory = mapper.getFactory();
        List<Ticket> ticketList = new ArrayList<>();

        if (path == null || path.isBlank()) {
            log.error("Путь к файлу не указан");
            return List.of();
        }
        File file = new File(path);
        if (!file.isFile() || !file.canRead()) {
            log.error("Файл не найден: {}", path);
            return List.of();
        }

        try (JsonParser parser = factory.createParser(file)) {
            parseJsonFile(parser, ticketList);
        } catch (IOException e) {
            log.error("Ошибка при чтении файла");
        }
        return ticketList;
    }

    /**
     * Парсит JSON-файл, проверяя его базовую структуру
     *
     * @param parser JSON-парсер
     * @param ticketList список для добавления билетов
     * @throws IOException при ошибках чтения файла
     */
    private void parseJsonFile(JsonParser parser, List<Ticket> ticketList) throws IOException {
        if (parser.nextToken() != JsonToken.START_OBJECT) {
            log.error("Неверный формат JSON: должен начинаться с объекта");
            return;
        }

        while (parser.nextToken() != JsonToken.END_OBJECT) {
            String fieldName = parser.currentName();
            if (nameOfJsonStartField.equals(fieldName)) {
                parseTicketsArray(parser, ticketList);
            } else {
                log.warn(" JSON начинается с неизвестного поля: {}", fieldName);
                parser.skipChildren();
            }
        }
    }

    /**
     * Обрабатывает массив билетов в JSON-файле
     *
     * @param parser JSON-парсер
     * @param ticketList список для добавления билетов
     * @throws IOException при ошибках чтения файла
     */
    private void parseTicketsArray(JsonParser parser, List<Ticket> ticketList) throws IOException {
        parser.nextToken();
        if (parser.currentToken() != JsonToken.START_ARRAY) {
            log.error("Поле '{}' должно содержать массив", nameOfJsonStartField);
            return;
        }

        while (parser.nextToken() != JsonToken.END_ARRAY) {

            parseSingleTicket(parser, ticketList);
        }
    }

    /**
     * Парсит отдельный билет из JSON и добавляет его в список после валидации
     *
     * @param parser JSON-парсер
     * @param ticketList список билетов
     */
    private void parseSingleTicket(JsonParser parser, List<Ticket> ticketList) {
        try {
            Ticket ticket = mapper.readValue(parser, Ticket.class);
            if (isValidTicket(ticket)) {
                ticketList.add(ticket);
            }
        } catch (IOException e) {
            log.error("Ошибка парсинга билета");
        }
    }

    /**
     * Проверяет валидность объекта билета
     *
     * @param ticket объект билета для проверки
     * @return true если билет валиден, false если есть ошибки
     */
    private boolean isValidTicket(Ticket ticket) {

        if (ticket == null) {
            log.warn("Найден null-билет");
            return false;
        }

        if (ticket.getOrigin() == null || ticket.getOrigin().isBlank() ||
                ticket.getDestination() == null || ticket.getDestination().isBlank()) {
            log.warn("Билет с пустыми origin/destination: {}", ticket);
            return false;
        }

        if (ticket.getDepartureDate() == null || ticket.getArrivalDate() == null ||
                ticket.getDepartureTime() == null || ticket.getArrivalTime() == null) {
            log.warn("Билет с пустыми датами/временем: {}", ticket);
            return false;
        }

        if (ticket.getPrice() == null || ticket.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            log.warn("Билет с некорректной ценой: {}", ticket.getPrice());
            return false;
        }


        return isValidDateAndTime(ticket);
    }

    /**
     * Проверяет корректность форматов даты и времени в билете
     *
     * @param ticket объект билета для проверки
     * @return true если форматы корректны, false если есть ошибки
     */
    private boolean isValidDateAndTime(Ticket ticket) {
        try {
            LocalDate.parse(ticket.getDepartureDate(), DateTimeFormatter.ofPattern(dateFormat));
        } catch (DateTimeParseException e) {
            log.warn("Некорректный формат даты отправления: {}", ticket.getDepartureDate());
            return false;
        }

        try {
            LocalDate.parse(ticket.getArrivalDate(), DateTimeFormatter.ofPattern(dateFormat));
        } catch (DateTimeParseException e) {
            log.warn("Некорректный формат даты прибытия: {}", ticket.getArrivalDate());
            return false;
        }

        try {
            LocalTime.parse(ticket.getDepartureTime(), timeFormatter);
        } catch (DateTimeParseException e) {
            log.warn("Некорректный формат времени отправления: {}", ticket);
            return false;
        }

        try {
            LocalTime.parse(ticket.getArrivalTime(), timeFormatter);
        } catch (DateTimeParseException e) {
            log.warn("Некорректный формат времени прибытия: {}", ticket);
            return false;
        }
        return true;
    }

}
