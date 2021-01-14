package ru.otus.hw6;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class App {
    public static void main(String[] args) throws Exception {
        SpringApplication.run(App.class);
    }
}
