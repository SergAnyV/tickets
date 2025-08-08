package ru.anikeev.tickets.services.impl;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.anikeev.tickets.dto.TicketDTO;
import ru.anikeev.tickets.entities.Ticket;
import ru.anikeev.tickets.repositories.JsonReader;
import ru.anikeev.tickets.services.TicketServiceInternal;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Основная реализация сервиса для анализа авиабилетов.
 * Выполняет чтение и фильтрацию билетов по указанным аэропортам,
 * расчет разницы между средней ценой и медианой,
 */
@Service
public class TicketServiceImpl implements TicketServiceInternal {
    private final JsonReader jsonReader;
    private final String airportOrigin;
    private final String airportDestination;
    private final DateTimeFormatter dateTimeFormatter;

    public TicketServiceImpl(JsonReader jsonReader,
                             @Value("${airport.origin}") String airportOrigin,
                             @Value("${airport.destination}") String airportDestination,
                             @Qualifier("dateTimeFormatter") DateTimeFormatter dateTimeFormatter) {
        this.jsonReader = jsonReader;
        this.airportOrigin = airportOrigin;
        this.airportDestination = airportDestination;
        this.dateTimeFormatter = dateTimeFormatter;
    }

    /**
     * Основной метод анализа, возвращающий все рассчитанные показатели.
     * Включает разницу между средней ценой и медианой и минимальное время полета по авиакомпаниям.
     *
     * @param path путь к JSON-файлу с билетами
     * @return DTO с результатами анализа
     */
    @Override
    public TicketDTO resultOfAnalys(String path) {
        List<Ticket> ticketList = filterOriginToDestination(
                jsonReader.readJsonByPath(path)
        );

        BigDecimal priceDifference = findDifferrenceBetwenAverageAndMedian(ticketList);
        Map<String, Duration> mapOfCarrier = findMinTimeOfFlightBetweenOriginAndDistin(ticketList);
        return TicketDTO.builder()
                .differenceBetweenAverageAndMedianPrice(priceDifference)
                .mapMinTimeBetweenOriginDist(mapOfCarrier)
                .build();
    }

    /**
     * Вычисляет разницу между средней ценой и медианой стоимости билетов.
     *
     * @param tickets список билетов для анализа
     * @return разница между средней ценой и медианой (средняя - медиана)
     */
    @Override
    public BigDecimal findDifferrenceBetwenAverageAndMedian(List<Ticket> tickets) {
        List<BigDecimal> prices = tickets.stream().map(ticket -> ticket.getPrice()).toList();
        BigDecimal average = calculateAverage(prices);
        BigDecimal median = calculateMediana(prices);
        return average.subtract(median);
    }

    /**
     * Находит минимальное время полета для каждого авиаперевозчика.
     *
     * @param tickets список билетов для анализа
     * @return карта где ключ - код авиакомпании, значение - минимальное время полета
     */
    @Override
    public Map<String, Duration> findMinTimeOfFlightBetweenOriginAndDistin(List<Ticket> tickets) {
        return tickets.stream()
                .collect(
                        Collectors.toMap(
                                ticket -> ticket.getCarrier(),
                                ticket -> calculateTicketDuration(ticket), (t1, t2) -> {
                                    return t1.compareTo(t2) <= 0 ? t1 : t2;
                                }
                        )
                );
    }

    /**
     * Вычисляет продолжительность полета по билету.
     *
     * @param ticket билет для расчета
     * @return продолжительность полета
     */
    @Override
    public Duration calculateTicketDuration(Ticket ticket) {
        LocalDateTime departure = localDateTimeParser(ticket.getDepartureDate(), ticket.getDepartureTime());
        LocalDateTime arrive = localDateTimeParser(ticket.getArrivalDate(), ticket.getArrivalTime());
        return Duration.between(departure, arrive);
    }

    /**
     * Преобразует строковые дату и время в объект LocalDateTime.
     *
     * @param date строка с датой
     * @param time строка с временем
     * @return объединенный объект LocalDateTime
     */
    @Override
    public LocalDateTime localDateTimeParser(String date, String time) {
        return LocalDateTime.parse(date + " " + time, dateTimeFormatter);
    }

    /**
     * Фильтрует билеты по заданным аэропортам отправления и назначения.
     *
     * @param tickets полный список билетов
     * @return отфильтрованный список билетов
     */
    @Override
    public List<Ticket> filterOriginToDestination(List<Ticket> tickets) {
        return tickets.parallelStream()
                .filter(ticket ->
                        airportOrigin.equals(ticket.getOrigin())
                                && airportDestination.equals(ticket.getDestination()))
                .toList();
    }

    /**
     * Вычисляет среднюю цену билетов.
     *
     * @param prices список цен
     * @return среднее значение цен
     */
    @Override
    public BigDecimal calculateAverage(List<BigDecimal> prices) {
        BigDecimal sum = prices.parallelStream()
                .reduce(BigDecimal.ZERO, (bigDecimal, bigDecimal2) -> bigDecimal.add(bigDecimal2));
        return sum.divide(BigDecimal.valueOf(prices.size()), 2, RoundingMode.HALF_UP);
    }

    /**
     * Вычисляет медианную цену билетов.
     *
     * @param prices список цен
     * @return медианное значение цен
     */
    @Override
    public BigDecimal calculateMediana(List<BigDecimal> prices) {
        List<BigDecimal> sorted = new ArrayList<>(prices);
        Collections.sort(sorted);
        int size = sorted.size();
        if (size % 2 == 1) {
            return sorted.get(size / 2);
        }
        return sorted.get(size / 2 - 1)
                .add(sorted.get(size / 2))
                .divide(BigDecimal.valueOf(2), 2, RoundingMode.HALF_UP);
    }
}
