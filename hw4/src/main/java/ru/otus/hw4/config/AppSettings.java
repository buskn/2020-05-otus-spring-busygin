package ru.otus.hw4.config;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.boot.context.properties.bind.DefaultValue;

import java.nio.charset.Charset;
import java.util.List;
import java.util.Locale;

/**
 * Вот я даже не знаю. Хотелось бы в программе вызывать свойство,
 * например, "${application.some.useful.long.option}"
 * как settings.some.useful.long.option,
 * а не как settings.getSome().getUseful().getLong().getOption(),
 * но как это вяжется с правилами приличия?
 */
@ConfigurationProperties("hw3")
@ConstructorBinding
@ToString
@Getter
public class AppSettings {
    private final Questions questions;
    private final Testing testing;
    private final Ui ui;
    private final boolean debug;

    public AppSettings(
            @DefaultValue Questions questions,
            @DefaultValue Testing testing,
            @DefaultValue Ui ui,
            @DefaultValue("false") boolean debug)
    {
        this.questions = questions;
        this.testing = testing;
        this.ui = ui;
        this.debug = debug;
    }

    @ToString
    @Getter
    public static class Questions {
        private final String filename;
        private final Charset charset;
        private final char separator;
        private final char encloser;

        public Questions(
                @DefaultValue("questions_template.csv") String filename,
                @DefaultValue("UTF-8") Charset charset,
                @DefaultValue(";") char separator,
                @DefaultValue("\"") char encloser)
        {
            this.filename = filename;
            this.charset = charset;
            this.separator = separator;
            this.encloser = encloser;
        }
    }

    @ToString
    @Getter
    public static class Testing {
        private final double acceptableRatio;

        public Testing(@DefaultValue("0.59") double acceptableRatio) {
            this.acceptableRatio = acceptableRatio;
        }
    }

    @ToString
    @Setter
    @Getter
    public static class Ui {
        private Charset charset;
        private Locale locale;
        private List<Locale> acceptableLocale;

        public Ui(  @DefaultValue("UTF-8") Charset charset,
                    @DefaultValue("en") Locale locale,
                    @DefaultValue List<Locale> acceptableLocale )
        {
            this.charset = charset;
            this.locale = locale;
            this.acceptableLocale = acceptableLocale;
            System.out.println(acceptableLocale);
        }
    }
}
