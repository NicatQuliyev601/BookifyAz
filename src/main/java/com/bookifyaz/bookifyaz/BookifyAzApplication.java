package com.bookifyaz.bookifyaz;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class BookifyAzApplication {

    public static void main(String[] args) {
        SpringApplication.run(BookifyAzApplication.class, args);
    }

}
