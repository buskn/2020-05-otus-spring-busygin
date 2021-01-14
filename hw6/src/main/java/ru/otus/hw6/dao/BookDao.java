package ru.otus.hw6.dao;

import java.util.List;
import java.util.Optional;

public interface BookDao {
    List<Book> getAll();
    Optional<Book> getById(long id);
    List<Book> searchByTitlePart(String title);
    Book save(Book book);
    void delete(long id);
}
