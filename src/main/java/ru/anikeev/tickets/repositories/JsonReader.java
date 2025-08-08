package ru.anikeev.tickets.repositories;


import ru.anikeev.tickets.entities.Ticket;
import java.util.List;
/**
 * Интерфейс для чтения JSON-файлов с билетами.
 * Определяет контракт для реализации парсеров JSON-данных.
 */
public interface JsonReader {
    /**
     * Читает JSON-файл по указанному пути и возвращает список билетов
     *
     * @param path путь к JSON-файлу с билетами
     * @return список объектов Ticket или пустой список при ошибках
     */
    List<Ticket> readJsonByPath(String path);
}
