package ru.otus.hw5;

import org.h2.tools.Console;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
@ConfigurationPropertiesScan
public class App {
    public static void main(String[] args) throws Exception {
        SpringApplication.run(App.class);

//        Console.main(args);
    }
}
