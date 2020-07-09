package ru.otus.hw5.ui;

import org.springframework.shell.Availability;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellMethodAvailability;

public interface OperationCommands {
    void done();
    void cancel();
    void show();
}
