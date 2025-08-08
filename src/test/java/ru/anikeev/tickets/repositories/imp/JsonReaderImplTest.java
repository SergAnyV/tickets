package ru.anikeev.tickets.repositories.imp;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestConstructor;
import ru.anikeev.tickets.entities.Ticket;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@ActiveProfiles("test")
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
class JsonReaderImplTest {
    @Autowired
    private JsonReaderImpl jsonReader;

    private Path tempTestFile;

    @BeforeEach
    void setUp() throws IOException {

        String testJson = """
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
                """;

        tempTestFile = Files.createTempFile("test_tickets", ".json");
        Files.writeString(tempTestFile, testJson);
    }

    @AfterEach
    void tearDown() throws IOException {

        if (tempTestFile != null) {
            Files.deleteIfExists(tempTestFile);
        }
    }

    @Test
    void readJsonByPathShouldReturnTicketsWhenFileValid() {
        List<Ticket> result = jsonReader.readJsonByPath(tempTestFile.toString());
        assertEquals("VVO", result.get(0).getOrigin(), "Неверный origin первого билета");
        assertEquals("TLV", result.get(0).getDestination(), "Неверный destination первого билета");
        assertEquals(new BigDecimal("10000"), result.get(0).getPrice(), "Неверная цена билета");
    }

    @Test
    void readJsonByPathShouldReturnEmptyListWhenFileNotExists() {
        List<Ticket> result = jsonReader.readJsonByPath("non_existent_file.json");
        assertTrue(result.isEmpty(), "Для несуществующего файла должен вернуться пустой список");
    }

}