package ru.otus.hw6.data.dao;

import ru.otus.hw6.data.model.Genre;

import java.util.List;
import java.util.Optional;

public interface GenreDao {
    List<Genre> getAll();
    List<Genre> searchByGenrePart(String part);
    Genre getById(long id);
    Optional<Genre> getByGenre(String genre);
    void update(Genre genre);
    Genre insert(Genre genre);
    void delete(Genre genre);
}
