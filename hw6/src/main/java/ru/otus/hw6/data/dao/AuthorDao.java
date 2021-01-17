package ru.otus.hw6.data.dao;

import ru.otus.hw6.data.model.Author;

import java.util.List;
import java.util.Optional;

public interface AuthorDao {
    List<Author> getAll();
    Optional<Author> getByName(String name);
    List<Author> searchByNamePart(String name);
    Author getById(long id);
    void update(Author author);
    Author insert(Author author);
    void delete(Author author);
}
