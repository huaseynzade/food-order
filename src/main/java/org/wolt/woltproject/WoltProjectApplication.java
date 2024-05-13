package org.wolt.woltproject;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@SpringBootApplication
//@EnableScheduling
@EnableWebSecurity
public class WoltProjectApplication {

    public static void main(String[] args) {

        SpringApplication.run(WoltProjectApplication.class, args);
    }

}
