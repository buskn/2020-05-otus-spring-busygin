package ru.otus.hw6.data.dao;

import ru.otus.hw6.data.model.Book;

import java.util.List;
import java.util.Optional;

public interface BookDao {
    List<Book> getAll();
    Optional<Book> getById(long id);
    List<Book> searchByTitlePart(String title);
    Book save(Book book);
    void delete(Book book);
}
