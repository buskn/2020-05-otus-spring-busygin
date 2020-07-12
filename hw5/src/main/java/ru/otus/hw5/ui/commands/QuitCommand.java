package ru.otus.hw5.ui.commands;

import lombok.RequiredArgsConstructor;
import org.springframework.shell.ExitRequest;
import org.springframework.shell.standard.ShellCommandGroup;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.commands.Quit;
import ru.otus.hw5.ui.Usage;

@ShellComponent
@ShellCommandGroup("Common Commands")
@RequiredArgsConstructor
public class QuitCommand implements Quit.Command {
    @ShellMethod(value = "shell.command.quit", key = {"quit", "exit"})
    @Usage("shell.command.quit.usage")
    public void quit() {
        throw new ExitRequest();
    }
}
