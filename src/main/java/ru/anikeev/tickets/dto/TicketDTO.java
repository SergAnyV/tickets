package ru.anikeev.tickets.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.Map;

@Data
@Builder
public class TicketDTO {
    private BigDecimal differenceBetweenAverageAndMedianPrice;
    private Map<String, Duration> mapMinTimeBetweenOriginDist;

    public void printResults() {
        System.out.println("\nРезультаты анализа билетов");
        System.out.printf("Разница между средней ценой и медианой: %.2f \n", differenceBetweenAverageAndMedianPrice);

        System.out.println("\nМинимальное время полета по авиакомпаниям:");
        mapMinTimeBetweenOriginDist.forEach((carrier, duration) -> {
            long hours = duration.toHours();
            long minutes = duration.toMinutesPart();
            System.out.printf("- %s: %d ч %d мин\n", carrier, hours, minutes);
        });
    }

    }
