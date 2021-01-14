package ru.otus.hw6.ui.commands;

import lombok.RequiredArgsConstructor;
import org.springframework.shell.Availability;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellMethodAvailability;
import org.springframework.shell.standard.ShellOption;
import ru.otus.hw6.config.Settings;
import ru.otus.hw6.ui.IO;
import ru.otus.hw6.ui.ShellState;

import java.util.Locale;

@ShellComponent
@RequiredArgsConstructor
public class OperationManagementCommands {
    private final IO io;
    private final Settings settings;
    private final ShellState state;

    @ShellMethod(value = "shell.command.language", key = "language")
    public void language(@ShellOption(defaultValue = "") Locale locale) {
        if (locale == null) {
            showCurrentLocale();
        }
        else {
            changeLocale(locale);
        }
    }

    private void showCurrentLocale() {
        io.interPrint("shell.locale.current")
                .println(settings.getUi().getLocale())
                .interPrint("shell.locale.acceptable")
                .println(settings.getUi().getAcceptableLocale());
    }

    private void changeLocale(Locale locale) {
        if (settings.getUi().getAcceptableLocale().contains(locale)) {
            settings.getUi().setLocale(locale);
            io.interPrintln("shell.locale.changed", locale);
        }
        else {
            io.interPrintln("shell.language.not-supported", locale);
        }
    }

    @ShellMethod(value = "shell.command.done", key = "done")
    @ShellMethodAvailability("operationManagementAvailability")
    public void done() {
        state.getOperationCommands().done();
    }

    public Availability operationManagementAvailability() {
        return state.getState() != ShellState.State.ROOT ? Availability.available()
                : Availability.unavailable("no operation is in process");
    }

    @ShellMethod(value = "shell.command.cancel", key = "cancel")
    @ShellMethodAvailability("operationManagementAvailability")
    public void cancel() {
        state.getOperationCommands().cancel();
    }

    @ShellMethod(value = "shell.command.show", key = "show")
    @ShellMethodAvailability("operationManagementAvailability")
    public void show() {
        state.getOperationCommands().show();
    }

    private void showSeparator() {
        io.println("===========================================");
    }
}
