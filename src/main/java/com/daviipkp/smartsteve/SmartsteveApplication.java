package com.daviipkp.smartsteve;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SmartsteveApplication {

    public static void main(String[] args) {

        new SpringApplicationBuilder(SmartsteveApplication.class)
                .headless(false)
                .run(args);
    }
}
