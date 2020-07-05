package ru.otus.hw5.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.boot.context.properties.bind.DefaultValue;

import java.util.Locale;

@ConfigurationProperties(prefix = "hw")
@ConstructorBinding
@Getter
public class Settings {
    private final Ui ui;

    public Settings(@DefaultValue Ui ui) {
        this.ui = ui;
    }

    @Getter
    @Setter
    public static class Ui {
        private Locale locale;

        public Ui(@DefaultValue("en") Locale locale) {
            this.locale = locale;
        }
    }
}
