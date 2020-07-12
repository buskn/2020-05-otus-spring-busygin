package ru.otus.hw5.ui.commands;

import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.Shell;
import org.springframework.shell.standard.ShellCommandGroup;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;
import org.springframework.shell.standard.commands.Help;
import ru.otus.hw5.ui.IO;
import ru.otus.hw5.ui.Usage;

import java.lang.reflect.Method;
import java.util.*;

import static java.util.Comparator.comparing;

@ShellComponent
@ShellCommandGroup("Common Commands")
@RequiredArgsConstructor
public class HelpCommand implements Help.Command {
    private final IO io;

    @Autowired
    private Shell shell;

    @ShellMethod(value = "shell.command.help", key = "help")
    @Usage("shell.command.help.usage")
    public void help(@ShellOption(defaultValue = "") String command) {
        if ("".equals(command)) {
            printCommandList();
        }
        else {
            printCommandUsage(command);
        }
    }

    private void printCommandList() {
        io.interPrintln("shell.help.header");

        val aliasSet = new HashMap<Method, Set<String>>();
        val groups = new TreeMap<String, Set<Method>>();
        val helps = new HashMap<Method, String>();
        shell.listCommands().forEach( (command, target) -> {
            if (target.getAvailability().isAvailable()) {
                aliasSet.computeIfAbsent(target.getMethod(), x -> new TreeSet<>()).add(command);
                groups.computeIfAbsent(target.getGroup(), x -> new HashSet<>()).add(target.getMethod());
                helps.put(target.getMethod(), target.getHelp());
            }
        });

        val aliases = new HashMap<Method, String>();
        aliasSet.forEach( (method, set) -> aliases.put(method, String.join(", ", set)));

        groups.forEach( (group, methodSet) -> {
            io.println(group);
            val methods = new TreeSet<Method>(comparing(aliases::get));
            methods.addAll(methodSet);

            val length = aliases.values().stream().mapToInt(String::length).max().orElse(0);

            methods.forEach(method ->
                    io.printf("    %-" + length +"s - %s\n",
                            aliases.get(method), io.inter(helps.get(method))));
        });
    }

    private void printCommandUsage(String command) {
        shell.listCommands().entrySet().stream()
                .filter(e -> e.getKey().equals(command))
                .map(Map.Entry::getValue)
                .findFirst().ifPresentOrElse(
                target -> {
                    val annot = target.getMethod().getAnnotation(Usage.class);
                    io.interPrintln(annot != null && !"".equals(annot.value())
                            ? annot.value() : "shell.help.usage.not-found", command);
                },
                () -> io.interPrintln("shell.help.command.not-found", command)
        );
    }
}
