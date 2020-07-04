package ru.otus.hw5.dao;

import java.util.List;

public interface GenreDao {
    List<Genre> getAll();
    List<Genre> getAllByBookId(long bookId);
    Genre getById(long id);
    void update(Genre genre);
    Genre insert(Genre genre);
    void delete(long id);
}
