package ru.otus.hw5.ui;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.Shell;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.commands.Help;

import java.util.Map;

@ShellComponent
@RequiredArgsConstructor
public class HelpCommand implements Help.Command {
    private final IO io;

    @Autowired
    private Shell shell;

    @ShellMethod(value = "help")
    public void help() {
        io.interPrintln("shell.command.help");
        shell.listCommands().entrySet().stream()
                .filter(e -> e.getValue().getAvailability().isAvailable())
                .sorted(Map.Entry.comparingByKey())
                .forEach(e -> io.print(e.getKey() + ":\t").interPrintln(e.getValue().getHelp()));
    }
}
