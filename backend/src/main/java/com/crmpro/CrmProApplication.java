package com.crmpro;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class CrmProApplication {

    public static void main(String[] args) {
        SpringApplication.run(CrmProApplication.class, args);
    }
}
