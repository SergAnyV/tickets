package ru.anikeev.tickets.services.impl;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestConstructor;
import ru.anikeev.tickets.dto.TicketDTO;
import ru.anikeev.tickets.entities.Ticket;
import ru.anikeev.tickets.services.TicketServiceInternal;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
class TicketServiceImplTest {
    @Autowired
    private TicketServiceInternal ticketService;

    private List<Ticket> testTickets;
    private List<Ticket> mixedOriginDestinationTickets;
    private Ticket testTicketForDuration;

    @BeforeEach
    void setUp() {
        // Основные тестовые билеты для проверки расчетов
        testTickets = List.of(
                createTestTicket("VVO", "TLV", "SU",
                        "10000", "12.05.18", "10:00", "12.05.18", "18:00"),
                createTestTicket("VVO", "TLV", "SU",
                        "15000", "12.05.18", "11:00", "12.05.18", "17:00"),
                createTestTicket("VVO", "TLV", "TK",
                        "20000", "12.05.18", "09:00", "12.05.18", "21:00")
        );

        // Билеты с разными origin/destination
        mixedOriginDestinationTickets = List.of(
                createTestTicket("VVO", "TLV", "SU", "10000", "12.05.18", "10:00", "12.05.18", "18:00"),
                createTestTicket("VVO", "UFA", "TK", "20000", "12.05.18", "09:00", "12.05.18", "21:00"),
                createTestTicket("LED", "TLV", "S7", "30000", "12.05.18", "08:00", "12.05.18", "20:00")
        );

        // Отдельный билет для проверки расчета длительности
        testTicketForDuration = createTestTicket(
                "VVO", "TLV", "SU", "10000",
                "12.05.18", "10:30", "12.05.18", "18:45"
        );
    }

    @Test
    void resultOfAnalysShouldReturnCorrectDto() throws IOException {
        Path testFile = Files.createTempFile("test_tickets", ".json");
        Files.writeString(testFile, """
        {
          "tickets": [
            {
              "origin": "VVO",
              "destination": "TLV",
              "departure_date": "12.05.18",
              "departure_time": "10:00",
              "arrival_date": "12.05.18",
              "arrival_time": "12:00",
              "carrier": "SU",
              "price": 10000
            }
          ]
        }
        """);

        TicketDTO result = ticketService.resultOfAnalys(testFile.toString());
        assertNotNull(result);

        Files.deleteIfExists(testFile);
    }

    @Test
    void findMinTimeOfFlightBetweenOriginAndDistinShouldFindMinDurations() {
        Map<String, Duration> result = ticketService.findMinTimeOfFlightBetweenOriginAndDistin(testTickets);
        assertEquals(2, result.size());
        assertEquals(Duration.ofHours(6), result.get("SU"));
        assertEquals(Duration.ofHours(12), result.get("TK"));
    }

    @Test
    void filterOriginToDestinationShouldFilterCorrectly() {
        List<Ticket> result = ticketService.filterOriginToDestination(mixedOriginDestinationTickets);
        assertEquals(1, result.size());
        assertEquals("SU", result.get(0).getCarrier());
        assertEquals("VVO", result.get(0).getOrigin());
        assertEquals("TLV", result.get(0).getDestination());
    }

    @Test
    void calculateTicketDurationShouldCalculateCorrectDuration() {
        Duration result = ticketService.calculateTicketDuration(testTicketForDuration);
        assertEquals(Duration.ofHours(8).plusMinutes(15), result);
    }

    private Ticket createTestTicket(String origin, String destination, String carrier,
                                    String price, String depDate, String depTime,
                                    String arrDate, String arrTime) {
        Ticket ticket = new Ticket();
        ticket.setOrigin(origin);
        ticket.setDestination(destination);
        ticket.setCarrier(carrier);
        ticket.setPrice(new BigDecimal(price));
        ticket.setDepartureDate(depDate);
        ticket.setDepartureTime(depTime);
        ticket.setArrivalDate(arrDate);
        ticket.setArrivalTime(arrTime);
        return ticket;
    }
}