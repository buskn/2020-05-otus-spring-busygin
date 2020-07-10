package ru.otus.hw5.ui;

import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.Shell;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.commands.Help;

import java.lang.reflect.Method;
import java.util.*;

import static java.util.Comparator.comparing;

@ShellComponent
@RequiredArgsConstructor
public class HelpCommand implements Help.Command {
    private final IO io;

    @Autowired
    private Shell shell;

    @ShellMethod(value = "help")
    public void help() {
        io.interPrintln("shell.command.help");

        val aliases = new HashMap<Method, Set<String>>();
        val groups = new TreeMap<String, Set<Method>>();
        val helps = new HashMap<Method, String>();
        shell.listCommands().forEach( (command, target) -> {
            if (target.getAvailability().isAvailable()) {
                aliases.computeIfAbsent(target.getMethod(), x -> new TreeSet<>()).add(command);
                groups.computeIfAbsent(target.getGroup(), x -> new HashSet<>()).add(target.getMethod());
                helps.put(target.getMethod(), target.getHelp());
            }
        });

        val aliasesLine = new HashMap<Method, String>();
        aliases.forEach( (method, set) -> aliasesLine.put(method, String.join(", ", set)));

        groups.forEach( (group, methodSet) -> {
            io.println(group);
            val methods = new TreeSet<Method>(comparing(aliasesLine::get));
            methods.addAll(methodSet);

            val length = aliasesLine.values().stream().mapToInt(String::length).max().orElse(0);

            methods.forEach(method ->
                    io.printf("    %-" + length +"s - %s\n",
                            aliasesLine.get(method), io.inter(helps.get(method))));
        });
    }
}
