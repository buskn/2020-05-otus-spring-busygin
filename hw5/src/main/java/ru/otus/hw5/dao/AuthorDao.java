package ru.otus.hw5.dao;

import java.util.List;

public interface AuthorDao {
    List<Author> getAll();
    Author getById(long id);
    void update(Author author);
    Author insert(Author author);
    void delete(long id);
}
