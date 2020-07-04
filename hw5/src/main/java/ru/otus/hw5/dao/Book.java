package ru.otus.hw5.dao;

import lombok.Value;

import java.util.List;

@Value
public class Book {
    private final long id;
    private final String title;
    private final Author author;
    private final List<Genre> genres;

    public Book copyWithNewId(long id) {
        return new Book(id, title, author, genres);
    }
}
