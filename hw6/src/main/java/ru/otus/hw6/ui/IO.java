package ru.otus.hw6.ui;

import lombok.Getter;
import lombok.val;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import ru.otus.hw6.common.HwException;
import ru.otus.hw6.config.Settings;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

@Service
@Getter
public class IO {
    private final PrintWriter writer;
    private final Scanner scanner;
    private final MessageSource messageSource;
    private final Settings settings;

    public IO(@Value("#{ T(java.lang.System).out }") OutputStream outputStream,
              @Value("#{ T(java.lang.System).in }") InputStream inputStream,
              MessageSource messageSource, Settings settings) {
        writer = new PrintWriter(outputStream);
        scanner = new Scanner(inputStream);
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

    public IO printf(String fmt, Object ... params) {
        writer.printf(fmt, params);
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

    public String readLine() {
        return scanner.nextLine().trim();
    }

    public String readMultilineString() {
        String input;
        List<String> lines = new ArrayList<>();
        while ( ! (input = readLine()).equals("") )
            lines.add(input);
        return String.join("\n", lines);
    }

    public int readIntInBounds(int lower, int high) {
        val line = readLine();
        int num;

        try {
            num = Integer.parseInt(line);
        }
        catch (NumberFormatException e) {
            throw new HwException("shell.io.must-be-number");
        }

        if (num < lower || num > high)
            throw new HwException("shell.io.must-be-in-bounds", lower, high);

        return num;
    }
}
