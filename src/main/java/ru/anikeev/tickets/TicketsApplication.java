package ru.anikeev.tickets;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;
import ru.anikeev.tickets.dto.TicketDTO;
import ru.anikeev.tickets.repositories.JsonReader;
import ru.anikeev.tickets.repositories.imp.JsonReaderImpl;
import ru.anikeev.tickets.services.TicketService;

import java.util.Scanner;

@SpringBootApplication
@RequiredArgsConstructor
public class TicketsApplication {

    public static void main(String[] args) {
        SpringApplication.run(TicketsApplication.class, args);
    }

    @Bean
    @Profile("prod")
    public CommandLineRunner run(TicketService ticketService) {
        return args -> {

            Scanner scanner = new Scanner(System.in);
            System.out.println("Введите путь к файлу tickets.json ");
            String path = scanner.nextLine();

            TicketDTO result = ticketService.resultOfAnalys(path);
            result.printResults();
        };
    }
}

