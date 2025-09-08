package com.example.starlet_be;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class StarletBeApplication {

    public static void main(String[] args) {
        SpringApplication.run(StarletBeApplication.class, args);
    }

}
