package ru.otus.hw4.gui;

import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.shell.Availability;
import ru.otus.hw4.config.AppSettings;
import ru.otus.hw4.core.Sphinx;
import ru.otus.hw4.core.credentials.Credentials;

import java.util.List;
import java.util.Locale;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class CommandShellTest {

    @Configuration
    @Import(CommandShell.class)
    static class Config {}

    @SpyBean private CommandShell shell;

    @MockBean private IO io;
    @MockBean private AppSettings settings;
    @MockBean private AppSettings.Ui uiSettings;
    @MockBean private Credentials credentials;
    @MockBean private Sphinx sphinx;

    @BeforeEach
    private void setUp() {
        when(settings.getUi()).thenReturn(uiSettings);
    }

    @Test
    void givenNullArg_whenLanguage_thenShowCurrent() {
        List<Locale> acceptableLocale = List.of(Locale.CHINESE, Locale.JAPAN);
        Locale currentLocale = Locale.FRANCE;

        when(uiSettings.getAcceptableLocale()).thenReturn(acceptableLocale);
        when(uiSettings.getLocale()).thenReturn(currentLocale);

        shell.language(null);

        val codeArg = ArgumentCaptor.forClass(String.class);
        val objArg = ArgumentCaptor.forClass(Object.class);
        verify(io, times(2))
                .interPrintln(codeArg.capture(), objArg.capture());
        assertThat(codeArg.getAllValues())
                .contains("shell.locale.current", "shell.locale.supported");
        assertThat(objArg.getAllValues())
                .contains(acceptableLocale, currentLocale);
    }

    @Test
    void givenUnsupportedLocale_whenLanguage_thenShowNotSupported() {
        List<Locale> acceptableLocale = List.of(Locale.CHINESE, Locale.JAPAN);
        Locale newLocale = Locale.FRANCE;

        when(uiSettings.getAcceptableLocale()).thenReturn(acceptableLocale);

        shell.language(newLocale);

        val codeArg = ArgumentCaptor.forClass(String.class);
        val objArg = ArgumentCaptor.forClass(Object.class);
        verify(io, times(1))
                .interPrintln(codeArg.capture(), objArg.capture());
        assertThat(codeArg.getValue())
                .isEqualTo("shell.locale.not-supported");
        assertThat(objArg.getValue())
                .isEqualTo(newLocale);
    }

    @Test
    void givenSupportedLocale_whenLanguage_thenSuccess() {
        List<Locale> acceptableLocale = List.of(Locale.CHINESE, Locale.JAPAN, Locale.FRANCE);
        Locale newLocale = Locale.FRANCE;

        when(uiSettings.getAcceptableLocale()).thenReturn(acceptableLocale);

        shell.language(newLocale);

        val localeArg = ArgumentCaptor.forClass(Locale.class);
        verify(uiSettings, times(1)).setLocale(localeArg.capture());
        assertThat(localeArg.getValue()).isEqualTo(newLocale);

        val codeArg = ArgumentCaptor.forClass(String.class);
        val objArg = ArgumentCaptor.forClass(Object.class);
        verify(io, times(1))
                .interPrintln(codeArg.capture(), objArg.capture());
        assertThat(codeArg.getValue())
                .isEqualTo("shell.locale.changed");
        assertThat(objArg.getValue())
                .isEqualTo(newLocale);
    }

    //@Test
    // TODO bug in the shell spy?
    void givenEmptyUsername_whenLogin_thenAskUsernameAndSetCredentials() {
        String name = "", surname = "";
        String realName = "realName", realSurname = "realSurname";
        when(shell.askUserSurname()).thenReturn(realSurname);
        when(shell.askUserName()).thenReturn(realName);

        shell.login(name, surname);

        verify(shell, times(1 + 1)).askUserName();
        verify(shell, times(1 + 1)).askUserSurname();

        val argName = ArgumentCaptor.forClass(String.class);
        verify(credentials, times(1)).setName(argName.capture());
        assertThat(argName.getValue()).isEqualTo(realName);

        val argSurname = ArgumentCaptor.forClass(String.class);
        verify(credentials, times(1)).setSurname(argSurname.capture());
        assertThat(argSurname.getValue()).isEqualTo(realSurname);

    }

    @Test
    void givenUsername_whenLogin_thenAskUsernameAndSetCredentials() {
        String name = "realName", surname = "realSurname";

        shell.login(name, surname);

        val argName = ArgumentCaptor.forClass(String.class);
        verify(credentials, times(1)).setName(argName.capture());
        assertThat(argName.getValue()).isEqualTo(name);

        val argSurname = ArgumentCaptor.forClass(String.class);
        verify(credentials, times(1)).setSurname(argSurname.capture());
        assertThat(argSurname.getValue()).isEqualTo(surname);
    }

    @Test
    void givenNonEmptyName_whenAskUserName_thenReturnName() {
        String name = "Just a name";
        when(io.readLine()).thenReturn(name);

        assertThat(shell.askUserName()).isEqualTo(name);

        val arg = ArgumentCaptor.forClass(String.class);
        verify(io, times(1)).interPrint(arg.capture());
        assertThat(arg.getValue()).isEqualTo("enter.name");
        verify(io, times(1)).readLine();
    }

    @Test
    void givenOneEmptyName_whenAskUserName_thenReenterNameAndReturnName() {
        String name = "Just a name";
        when(io.readLine())
                .thenReturn("")
                .thenReturn(name);

        assertThat(shell.askUserName()).isEqualTo(name);

        val arg = ArgumentCaptor.forClass(String.class);
        verify(io, times(2)).interPrint(arg.capture());
        assertThat(arg.getAllValues()).containsExactly("enter.name", "enter.nonempty.string");
        verify(io, times(2)).readLine();
    }

    @Test
    void givenNonEmptyName_whenAskUserSurname_thenReturnName() {
        String surname = "Just a surname";
        when(io.readLine()).thenReturn(surname);

        assertThat(shell.askUserSurname()).isEqualTo(surname);

        val arg = ArgumentCaptor.forClass(String.class);
        verify(io, times(1)).interPrint(arg.capture());
        assertThat(arg.getValue()).isEqualTo("enter.surname");
        verify(io, times(1)).readLine();
    }

    @Test
    void givenOneEmptyName_whenAskUserSurname_thenReenterNameAndReturnName() {
        String name = "Just a surname";
        when(io.readLine())
                .thenReturn("")
                .thenReturn(name);

        assertThat(shell.askUserSurname()).isEqualTo(name);

        val arg = ArgumentCaptor.forClass(String.class);
        verify(io, times(2)).interPrint(arg.capture());
        assertThat(arg.getAllValues()).containsExactly("enter.surname", "enter.nonempty.string");
        verify(io, times(2)).readLine();
    }


    @Test
    void whenTesting_thenInvoke() {
        shell.testing();

        verify(sphinx, times(1)).doTesting();
    }

    @Test
    void givenNotLogged_whenTestAvailability_thenReturnNotAvailable() {
        String reason = "must be logged";
        when(credentials.isLoggedIn()).thenReturn(false);
        when(io.inter(any())).thenReturn(reason);
        assertThat(shell.testAvailability().isAvailable()).isEqualTo(false);
    }

    @Test
    void givenLogged_whenTestAvailability_thenReturnAvailable() {
        when(credentials.isLoggedIn()).thenReturn(true);
        assertThat(shell.testAvailability().isAvailable()).isEqualTo(true);
    }
}