package ru.otus.hw4.gui;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.otus.hw4.config.AppSettings;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Scanner;

@Service
public class IO {
    private final Scanner scanner;
    private final PrintWriter printer;
    private final MessageService messageService;

    public IO(
            @Value("#{T(java.lang.System).in}") InputStream input,
            @Value("#{T(java.lang.System).out}") OutputStream output,
            MessageService messageService,
            AppSettings settings
    ) {
        scanner = new Scanner(input, settings.getUi().getCharset());
        printer = new PrintWriter(output, true, settings.getUi().getCharset());
        this.messageService = messageService;
    }

    public void print(Object o) {
        printer.print(o);
        printer.flush();
    }

    public void println(Object o) {
        printer.println(o);
        printer.flush();
    }

    public void println() {
        printer.println();
        printer.flush();
    }

    public void interPrint(String str, Object ... args) {
        printer.print(messageService.get(str, args));
        printer.flush();
    }

    public void interPrintln(String str, Object ... args) {
        printer.println(messageService.get(str, args));
        printer.flush();
    }

    public String readLine() {
        return scanner.nextLine().trim();
    }

}
