package ru.otus.hw6.ui.commands;

import lombok.RequiredArgsConstructor;
import org.springframework.shell.Availability;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellMethodAvailability;
import ru.otus.hw6.data.model.Genre;
import ru.otus.hw6.services.GenreService;
import ru.otus.hw6.ui.IO;
import ru.otus.hw6.ui.ShellState;
import ru.otus.hw6.ui.Usage;

import static java.util.Comparator.comparing;

@ShellComponent
@RequiredArgsConstructor
public class GenreCommands {
    private final GenreService genreService;
    private final IO io;
    private final ShellState state;

    @ShellMethod(value = "shell.command.all-genres", key = "all-genres")
    @ShellMethodAvailability("onlyFromRootAvailable")
    @Usage("shell.command.all-genres.usage")
    public void allGenres() {
        io.interPrintln("shell.genre.all");
        genreService.getAll().stream().sorted(comparing(Genre::getGenre)).forEach(genre -> {
            io.printf("%s (%s)\n", genre.getGenre(), genre.getId());
        });
    }

    public Availability onlyFromRootAvailable() {
        return state.getState() == ShellState.State.ROOT
                ? Availability.available()
                : Availability.unavailable(
                        io.inter("shell.command.all-genres.unavailable-reason"));
    }
}
