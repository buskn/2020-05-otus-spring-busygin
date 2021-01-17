package ru.otus.hw6.data;

import lombok.Getter;
import ru.otus.hw6.data.model.Author;
import ru.otus.hw6.data.model.Book;
import ru.otus.hw6.data.model.Comment;
import ru.otus.hw6.data.model.Genre;

import java.util.ArrayList;
import java.util.List;

public class TestData {
    @Getter
    private static long authorNextId = 1;
    public static final Author
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

    public static final List<Author> AUTHORS = List.of(
            AUTHOR_1, AUTHOR_2, AUTHOR_3, AUTHOR_4, AUTHOR_5, AUTHOR_6, AUTHOR_FOR_DELETE);

    @Getter
    private static long genreNextId = 1;

    public static final Genre
            GENRE_1 = new Genre(genreNextId++, "genre1"),
            GENRE_2 = new Genre(genreNextId++, "genre2"),
            GENRE_3 = new Genre(genreNextId++, "genre3"),
            GENRE_4 = new Genre(genreNextId++, "genre4"),
            GENRE_5 = new Genre(genreNextId++, "_another_genre5"),
            GENRE_6 = new Genre(genreNextId++, "_ANOTHER_genre6"),
            GENRE_6_UPDATED = new Genre(GENRE_6.getId(), "_updated_genre6"),
            GENRE_FOR_DELETE = new Genre(genreNextId++, "genre_for_delete"),
            GENRE_FOR_INSERT = new Genre(0, "genre_for_insert");

    public static final List<Genre> GENRES = List.of(
            GENRE_1, GENRE_2, GENRE_3, GENRE_4, GENRE_5, GENRE_6, GENRE_FOR_DELETE);

    @Getter
    private static long commentNextId = 1;

    public static final Comment
            COMMENT_1 = new Comment(commentNextId++, "comment1"),
            COMMENT_2 = new Comment(commentNextId++, "comment2"),
            COMMENT_3 = new Comment(commentNextId++, "comment3"),
            COMMENT_4 = new Comment(commentNextId++, "comment4"),
            COMMENT_4_UPDATED = new Comment(COMMENT_4.getId(), "updated_comment4"),
            COMMENT_FOR_DELETE = new Comment(commentNextId++, "comment_for_delete"),
            COMMENT_FOR_INSERT = new Comment(0, "comment_for_insert");

    public static final List<Comment> COMMENTS = List.of(
            COMMENT_1, COMMENT_2, COMMENT_3, COMMENT_4);

    @Getter
    private static long bookNextId = 1;

    public static final Book
            BOOK_1 = new Book(bookNextId++, "book1", AUTHOR_1,
            List.of(GENRE_1, GENRE_2), List.of(COMMENT_1, COMMENT_2, COMMENT_3)),
            BOOK_2 = new Book(bookNextId++, "some_book2", AUTHOR_2,
                    List.of(GENRE_2, GENRE_3), List.of(COMMENT_4, COMMENT_FOR_DELETE)),
            BOOK_3 = new Book(bookNextId++, "SoMe_book3", AUTHOR_2, List.of(), List.of()),
            BOOK_3_UPDATED = new Book(BOOK_3.getId(), "book3", AUTHOR_4,
                    List.of(GENRE_2, GENRE_3), List.of(COMMENT_1, COMMENT_3)),
            BOOK_FOR_DELETE = new Book(bookNextId++, "book_for_delete", AUTHOR_2, List.of(), List.of()),
            BOOK_FOR_INSERT = new Book(0, "book_for_insert", AUTHOR_4,
                    List.of(GENRE_2, GENRE_3), List.of(COMMENT_1, COMMENT_3));

    public static final List<Book> BOOKS = List.of(BOOK_1, BOOK_2, BOOK_3, BOOK_FOR_DELETE);
}
