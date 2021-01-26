package ru.otus.hw6.data;

import lombok.Getter;
import ru.otus.hw6.data.model.Author;
import ru.otus.hw6.data.model.Book;
import ru.otus.hw6.data.model.Comment;
import ru.otus.hw6.data.model.Genre;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TestData {
    @Getter
    private long authorNextId = 1;
    public final Author
            AUTHOR_1 = new Author(authorNextId++, "author1"),
            AUTHOR_2 = new Author(authorNextId++, "author2"),
            AUTHOR_3 = new Author(authorNextId++, "author3"),
            AUTHOR_4 = new Author(authorNextId++, "author4"),
            AUTHOR_5 = new Author(authorNextId++, "_another_author5"),
            AUTHOR_6 = new Author(authorNextId++, "_ANOTHER_author6"),
            AUTHOR_6_UPDATED = new Author(AUTHOR_6.getId(), "updated_another_author6"),
            AUTHOR_FOR_DELETE = new Author(authorNextId++, "author_for_delete"),
            AUTHOR_FOR_INSERT = new Author(0, "author_for_insert"),
            AUTHOR_UNKNOWN = new Author(0, "unknown");

    public final List<Author> AUTHORS = list(
            AUTHOR_1, AUTHOR_2, AUTHOR_3, AUTHOR_4, AUTHOR_5, AUTHOR_6, AUTHOR_FOR_DELETE);

    @Getter
    private long genreNextId = 1;

    public final Genre
            GENRE_1 = new Genre(genreNextId++, "genre1"),
            GENRE_2 = new Genre(genreNextId++, "genre2"),
            GENRE_3 = new Genre(genreNextId++, "genre3"),
            GENRE_4 = new Genre(genreNextId++, "genre4"),
            GENRE_5 = new Genre(genreNextId++, "_another_genre5"),
            GENRE_6 = new Genre(genreNextId++, "_ANOTHER_genre6"),
            GENRE_6_UPDATED = new Genre(GENRE_6.getId(), "_updated_genre6"),
            GENRE_FOR_DELETE = new Genre(genreNextId++, "genre_for_delete"),
            GENRE_FOR_INSERT = new Genre(0, "genre_for_insert");

    public final List<Genre> GENRES = list(
            GENRE_1, GENRE_2, GENRE_3, GENRE_4, GENRE_5, GENRE_6, GENRE_FOR_DELETE);

    @Getter
    private long bookNextId = 1;

    public final Book
            BOOK_1 = new Book(bookNextId++, "book1", AUTHOR_1,
            list(GENRE_1, GENRE_2)),
            BOOK_2 = new Book(bookNextId++, "some_book2", AUTHOR_2,
                    list(GENRE_2, GENRE_3)),
            BOOK_3 = new Book(bookNextId++, "SoMe_book3", AUTHOR_2, list()),
            BOOK_3_UPDATED = new Book(BOOK_3.getId(), "book3", AUTHOR_4,
                    list(GENRE_2, GENRE_3)),
            BOOK_FOR_DELETE = new Book(bookNextId++, "book_for_delete", AUTHOR_2, list()),
            BOOK_FOR_INSERT = new Book(0, "book_for_insert", AUTHOR_4,
                    list(GENRE_2, GENRE_3));

    public final List<Book> BOOKS = list(BOOK_1, BOOK_2, BOOK_3, BOOK_FOR_DELETE);

    @Getter
    private long commentNextId = 1;

    public final Comment
            COMMENT_1 = new Comment(commentNextId++, BOOK_1, "comment1"),
            COMMENT_2 = new Comment(commentNextId++, BOOK_1, "comment2"),
            COMMENT_3 = new Comment(commentNextId++, BOOK_1, "comment3"),
            COMMENT_4 = new Comment(commentNextId++, BOOK_2, "comment4"),
            COMMENT_4_UPDATED = new Comment(COMMENT_4.getId(), BOOK_3, "updated_comment4"),
            COMMENT_FOR_DELETE = new Comment(commentNextId++, BOOK_2, "comment_for_delete"),
            COMMENT_FOR_INSERT = new Comment(0, BOOK_3, "comment_for_insert");

    public final List<Comment> COMMENTS = list(
            COMMENT_1, COMMENT_2, COMMENT_3, COMMENT_4);

    @SafeVarargs
    private <T> List<T> list(T ... values) {
        return new ArrayList<>(Arrays.asList(values));
    }
}
