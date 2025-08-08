package ru.anikeev.tickets.services;

import ru.anikeev.tickets.dto.TicketDTO;

/**
 * Основной интерфейс сервиса для анализа билетов.
 * Определяет точку входа для анализа данных о билетах.
 */

public interface TicketService {
    /**
     * Анализирует билеты из JSON-файла и возвращает результаты
     *
     * @param path путь к JSON-файлу с билетами
     * @return DTO с результатами анализа
     */
    TicketDTO resultOfAnalys(String path) ;
}
