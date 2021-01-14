package ru.otus.hw6.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.boot.context.properties.bind.DefaultValue;

import java.util.List;
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
        private final List<Locale> acceptableLocale;

        public Ui(@DefaultValue("en") Locale locale,
                  @DefaultValue("en")List<Locale> acceptableLocale) {
            this.locale = locale;
            this.acceptableLocale = acceptableLocale;
        }
    }
}
