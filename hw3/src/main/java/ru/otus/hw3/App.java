package ru.otus.hw3;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.*;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import ru.otus.hw3.config.Localization;
import ru.otus.hw3.core.Sphinx;
import ru.otus.hw3.config.AppSettings;

@SpringBootApplication
@ConfigurationPropertiesScan
public class App {
    public static void main(String[] args) throws Exception {
        ApplicationContext ctx =
                SpringApplication.run(App.class);

        Sphinx sphinx = ctx.getBean("sphinx", Sphinx.class);
        sphinx.doTesting();
    }
}
