package ru.otus.hw5.dao;

import java.util.List;

public interface BookDao {
    List<Book> getAll();
    Book getById(long id);
    void update(Book book);
    Book insert(Book book);
    void delete(long id);
}
