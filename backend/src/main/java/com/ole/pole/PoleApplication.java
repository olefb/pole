package com.ole.pole;

import com.ole.pole.service.PollsDataInitializer;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class PoleApplication {
    public static void main(String[] args) {
        SpringApplication.run(PoleApplication.class, args);
    }

    @GetMapping("/hello")
    public String hello(@RequestParam(value = "name", defaultValue = "World") String name) {
        return String.format("Hello %s!", name);
    }

    @Bean
    public CommandLineRunner runDataInitialization(PollsDataInitializer initializer) {
        return args -> {
            initializer.initializeData();
        };
    }
}
