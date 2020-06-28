package ru.otus.hw4;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.ApplicationContext;
import ru.otus.hw4.core.Sphinx;

@SpringBootApplication
@ConfigurationPropertiesScan
public class App {
    public static void main(String[] args) throws Exception {
        SpringApplication.run(App.class);
    }
}
