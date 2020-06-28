package ru.otus.hw3.config;

import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.boot.context.properties.bind.DefaultValue;

import java.nio.charset.Charset;
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
public class AppSettings {
    public final Questions questions;
    public final Testing testing;
    public final Ui ui;
    public final boolean debug;

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
    public static class Questions {
        public final String filename;
        public final Charset charset;
        public final char separator;
        public final char encloser;

        public Questions(
                @DefaultValue("questions") String filename,
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
    public static class Testing {
        public final double acceptableRatio;

        public Testing(@DefaultValue("0.59") double acceptableRatio) {
            this.acceptableRatio = acceptableRatio;
        }
    }

    @ToString
    public static class Ui {
        public final Charset charset;
        public final Locale locale;

        public Ui(
                @DefaultValue("UTF-8") Charset charset,
                @DefaultValue("en") Locale locale)
        {
            this.charset = charset;
            this.locale = locale;
        }
    }
}
