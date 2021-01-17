package ru.otus.hw6.data.model;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.otus.hw6.data.model.Author;
import ru.otus.hw6.data.model.Book;
import ru.otus.hw6.data.model.Comment;
import ru.otus.hw6.data.model.Genre;

import java.util.List;

class BookTest {

    @Test
    void copyWithNewId() {
        Book book = new Book(
                1,
                "book1",
                new Author(1, "author1"),
                List.of(new Genre(1, "genre1")),
                List.of(new Comment(1, "a"))
        );
        Assertions.assertThat(book.copyWithNewId(2))
                .isEqualToIgnoringGivenFields(book, "id")
                .hasFieldOrPropertyWithValue("id", 2L);
    }
}