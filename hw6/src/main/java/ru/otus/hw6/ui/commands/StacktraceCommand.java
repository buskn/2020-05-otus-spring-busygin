package ru.otus.hw6.ui.commands;

import org.jline.terminal.Terminal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.shell.result.ThrowableResultHandler;
import org.springframework.shell.standard.ShellCommandGroup;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.commands.Stacktrace;
import ru.otus.hw6.ui.Usage;

@ShellComponent
@ShellCommandGroup("Common Commands")
public class StacktraceCommand implements Stacktrace.Command {
    @Autowired
    @Lazy
    private Terminal terminal;

    @Autowired @Lazy
    private ThrowableResultHandler throwableResultHandler;


    @ShellMethod(value = "shell.command.stacktrace", key = "stacktrace")
    @Usage("shell.command.stacktrace.usage")
    public void stacktrace() {
        if (throwableResultHandler.getLastError() != null) {
            throwableResultHandler.getLastError().printStackTrace(terminal.writer());
        }
    }
}
