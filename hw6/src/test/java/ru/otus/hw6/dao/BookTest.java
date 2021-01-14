package ru.otus.hw6.dao;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BookTest {

    @Test
    void copyWithNewId() {
        Book book = new Book(1, "book1", new Author(1, "author1"),
                List.of(new Genre(1, "genre1")));
        Assertions.assertThat(book.copyWithNewId(2))
                .isEqualToIgnoringGivenFields(book, "id")
                .hasFieldOrPropertyWithValue("id", 2L);
    }
}