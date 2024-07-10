package com.sparta.refrigerator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class RefrigeratorApplication {

    public static void main(String[] args) {
        SpringApplication.run(RefrigeratorApplication.class, args);
    }

}
