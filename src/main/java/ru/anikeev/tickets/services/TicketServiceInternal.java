package ru.anikeev.tickets.services;

import ru.anikeev.tickets.entities.Ticket;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
/**
 * Расширенный интерфейс сервиса для анализа билетов.
 * Содержит дополнительные методы для внутренней обработки данных.
 */
public interface TicketServiceInternal extends TicketService{
    /**
     * Находит разницу между средней ценой и медианой
     *
     * @param tickets список билетов для анализа
     * @return разница между средней ценой и медианой
     */
    BigDecimal findDifferrenceBetwenAverageAndMedian(List<Ticket> tickets);
    /**
     * Находит минимальное время полета для каждого перевозчика
     *
     * @param tickets список билетов для анализа
     * @return карта с минимальным временем полета по перевозчикам
     */
    Map<String, Duration> findMinTimeOfFlightBetweenOriginAndDistin(List<Ticket> tickets);
    /**
     * Вычисляет продолжительность полета по билету
     *
     * @param ticket билет для анализа
     * @return продолжительность полета
     */
    Duration calculateTicketDuration(Ticket ticket);
    /**
     * Парсит строковые дату и время в LocalDateTime
     *
     * @param date строка с датой
     * @param time строка с временем
     * @return объединенный объект LocalDateTime
     */
    LocalDateTime localDateTimeParser(String date, String time);
    /**
     * Фильтрует билеты по заданным аэропортам отправления и назначения
     *
     * @param tickets полный список билетов
     * @return отфильтрованный список билетов
     */
    List<Ticket> filterOriginToDestination(List<Ticket> tickets);
    /**
     * Вычисляет среднюю цену
     *
     * @param prices список цен
     * @return среднее значение цен
     */
    BigDecimal calculateAverage(List<BigDecimal> prices);
    /**
     * Вычисляет медиану цен
     *
     * @param prices список цен
     * @return медианное значение цен
     */
    BigDecimal calculateMediana(List<BigDecimal> prices);






}
