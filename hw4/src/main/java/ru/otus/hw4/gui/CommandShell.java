package ru.otus.hw4.gui;

import lombok.RequiredArgsConstructor;
import org.jline.utils.AttributedString;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.shell.Availability;
import org.springframework.shell.jline.PromptProvider;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellMethodAvailability;
import org.springframework.shell.standard.ShellOption;
import org.springframework.stereotype.Component;
import ru.otus.hw4.config.AppSettings;
import ru.otus.hw4.core.Sphinx;
import ru.otus.hw4.core.credentials.Credentials;
import ru.otus.hw4.utils.verification.BeanVerifier;
import ru.otus.hw4.utils.verification.VerificationEvent;

import java.util.Locale;

@ShellComponent
public class CommandShell {
    private final IO io;
    private final AppSettings settings;
    private final Credentials credentials;
    private final Sphinx sphinx;
    private final ApplicationEventPublisher publisher;

    public CommandShell(IO io,
                        AppSettings settings,
                        Credentials credentials,
                        Sphinx sphinx,
                        ApplicationEventPublisher publisher) {
        this.io = io;
        this.settings = settings;
        this.credentials = credentials;
        this.sphinx = sphinx;
        this.publisher = publisher;
    }

    @Component
    @RequiredArgsConstructor
    static class Prompt implements PromptProvider {
        private final Credentials credentials;

        @Override
        public AttributedString getPrompt() {
            return new AttributedString(
                    credentials.isLoggedIn()
                    ? credentials.getUsername() + "> "
                    : "not-logged> ");
        }
    }

    @ShellMethod(
            value = "change language or show language settings",
            key = {"language", "lang"})
    public void language(@ShellOption(defaultValue = "") Locale locale) {
        if ( locale != null ) {
            if (settings.getUi().getAcceptableLocale().contains(locale)) {
                settings.getUi().setLocale(locale);
                io.interPrintln("shell.locale.changed", locale);
                publisher.publishEvent(new VerificationEvent(this));
            }
            else {
                io.interPrintln("shell.locale.not-supported", locale);
            }
        }
        else {
            io.interPrintln("shell.locale.current",
                    settings.getUi().getLocale());
            io.interPrintln("shell.locale.supported", settings.getUi().getAcceptableLocale());
        }
    }


    @ShellMethod(value = "log in", key = "login")
    public void login(@ShellOption(defaultValue = "") String name,
                      @ShellOption(defaultValue = "") String surname)
    {
        if ("".equals(name) && "".equals(surname)) {
            name = askUserName();
            surname = askUserSurname();
        }

        credentials.setName(name);
        credentials.setSurname(surname);

        io.interPrintln("shell.login.success", credentials.getUsername());
    }


    public String askUserName() {
        String msg = "enter.name";
        String name;
        do {
            io.interPrint(msg);
            name = io.readLine();
            msg = "enter.nonempty.string";
        }
        while ("".equals(name));
        return name;
    }

    public String askUserSurname() {
        String msg = "enter.surname";
        String surname;
        do {
            io.interPrint(msg);
            surname = io.readLine();
            msg = "enter.nonempty.string";
        }
        while ("".equals(surname));
        return surname;
    }

    @ShellMethod(value = "perform testing", key = {"Testing","test"})
    @ShellMethodAvailability("testAvailability")
    public void testing() {
        sphinx.doTesting();
    }

    public Availability testAvailability() {
        return credentials.isLoggedIn()
                ? Availability.available()
                : Availability.unavailable(io.inter("shell.must-be-logged"));
    }

}
