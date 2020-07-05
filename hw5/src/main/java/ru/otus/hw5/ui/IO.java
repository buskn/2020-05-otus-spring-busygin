package ru.otus.hw5.ui;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import ru.otus.hw5.config.Settings;

import java.io.OutputStream;
import java.io.PrintWriter;

@Service
@Getter
public class IO {
    private final PrintWriter writer;
    private final MessageSource messageSource;
    private final Settings settings;

    public IO(@Value("#{ T(java.lang.System).out }") OutputStream outputStream,
              MessageSource messageSource, Settings settings) {
        writer = new PrintWriter(outputStream);
        this.messageSource = messageSource;
        this.settings = settings;
    }

    public IO print(Object o) {
        writer.print(o);
        writer.flush();
        return this;
    }

    public IO println() {
        writer.println();
        writer.flush();
        return this;
    }

    public IO println(Object o) {
        writer.println(o);
        writer.flush();
        return this;
    }

    public IO interPrint(String code, Object ... param) {
        return print(inter(code, param));
    }

    public IO interPrintln(String code, Object ... param) {
        return println(inter(code, param));
    }

    public String inter(String code, Object ... param) {
        return messageSource.getMessage(code, param, settings.getUi().getLocale());
    }
}
