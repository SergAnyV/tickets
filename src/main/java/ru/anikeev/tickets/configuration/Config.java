package ru.anikeev.tickets.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
/**
 * Конфигурационный класс для настройки бинов, используемых в приложении.
 * Содержит определения бинов для работы с JSON и форматирования даты/времени.
 */
@Configuration
public class Config {

    /**
     * Создает и возвращает бин ObjectMapper для работы с JSON.
     * ObjectMapper используется для сериализации и десериализации JSON-данных.
     *
     * @return настроенный экземпляр ObjectMapper
     */
    @Bean
    public ObjectMapper objectMapper(){

        return new ObjectMapper();
    }

    /**
     * Создает и возвращает бин DateTimeFormatter для форматирования времени.
     * Формат времени берется из properties-файла (параметр time.format).
     *
     * @param timeFormat строка формата времени (например, "HH:mm")
     * @return DateTimeFormatter для форматирования времени
     */
    @Bean(name = "timeFormatter")
    public DateTimeFormatter timeFormater(@Value("${time.format}") String timeFormat) {
        return new DateTimeFormatterBuilder()
                .appendPattern(timeFormat)
                .toFormatter();
    }

    /**
     * Создает и возвращает бин DateTimeFormatter для форматирования даты и времени.
     * Форматы даты и времени берутся из properties-файла (параметры date.format и time.format).
     *
     * @param dateFormat строка формата даты (например, "dd.MM.yy")
     * @param timeFormat строка формата времени (например, "HH:mm")
     * @return DateTimeFormatter для комбинированного форматирования даты и времени
     */
    @Bean(name = "dateTimeFormatter")
    public DateTimeFormatter dateTimeFormater(@Value("${date.format}") String dateFormat,
                                              @Value("${time.format}") String timeFormat){
        return new DateTimeFormatterBuilder()
                .appendPattern(dateFormat+" "+timeFormat)
                .toFormatter();
    }

}
