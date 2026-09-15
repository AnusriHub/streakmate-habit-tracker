package com.streakmate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class StreakMateApplication {
    public static void main(String[] args) {
        SpringApplication.run(StreakMateApplication.class, args);
    }
}
