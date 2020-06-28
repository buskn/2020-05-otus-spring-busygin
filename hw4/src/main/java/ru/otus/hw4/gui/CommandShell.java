package ru.otus.hw4.gui;

import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;
import ru.otus.hw4.config.AppSettings;

import java.util.Locale;

@ShellComponent
public class CommandShell {
    private final IO io;
    private final AppSettings.Ui ui;

    public CommandShell(IO io, AppSettings settings) {
        this.io = io;
        this.ui = settings.getUi();
    }

    @ShellMethod(
            value = "change language or show language settings",
            key = {"language", "lang"})
    public void language(@ShellOption(defaultValue = "") Locale locale) {
        if ( locale != null ) {
            if (ui.getAcceptableLocale().contains(locale)) {
                ui.setLocale(locale);
                io.interPrintln("shell.locale.changed", locale);
            }
            else {
                io.interPrintln("shell.locale.not-supported", locale);
            }
        }
        else {
            io.interPrintln("shell.current.locale", ui.getLocale());
            io.interPrintln("shell.supported.locales", ui.getAcceptableLocale());
        }
    }

}
