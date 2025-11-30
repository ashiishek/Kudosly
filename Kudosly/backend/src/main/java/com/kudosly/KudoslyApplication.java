package com.kudosly;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class KudoslyApplication {
    public static void main(String[] args) {
        SpringApplication.run(KudoslyApplication.class, args);
    }
}
