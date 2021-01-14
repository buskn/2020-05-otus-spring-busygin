package ru.otus.hw6.dao.jdbc;

import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import ru.otus.hw6.dao.Author;
import ru.otus.hw6.dao.Book;
import ru.otus.hw6.dao.Genre;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@JdbcTest
@Import({BookDaoJdbc.class, GenreDaoJdbc.class, AuthorDaoJdbc.class})
class BookDaoJdbcTest {

    @Autowired BookDaoJdbc daoOrig;
    BookDaoJdbc dao;

    @Autowired NamedParameterJdbcOperations jdbc;

    private final Book book1 = new Book(1, "book1", new Author(1, "author1"),
            List.of(new Genre(1, "genre1"), new Genre(2, "genre2")));
    private final Book book2 = new Book(2, "book2", new Author(2, "author2"),
            List.of(new Genre(2, "genre2"), new Genre(3, "genre3")));
    private final Book book3 = new Book(3, "book3", new Author(2, "author2"),
            List.of());
    private final List<Book> books = List.of(book1, book2, book3);

    @BeforeEach
    void setUp() {
        dao = spy(daoOrig);
    }

    @Test
    void givenNewBook_whenSave_thenInsert() {
        val newBook = book1.copyWithNewId(0);
        dao.save(newBook);
        verify(dao).insert(newBook);
    }

    @Test
    void givenExistBook_whenSave_thenUpdate() {
        dao.save(book2);
        verify(dao).update(book2);
    }

    @Test
    @DisplayName("возвращать все записи")
    void whenGetAll_thenSuccess() {
        assertThat(dao.getAll()).containsExactlyInAnyOrderElementsOf(books);
    }

    @Test
    @DisplayName("возвращать запись по существующему идентификатору")
    void givenExistId_whenGetById_thenSuccess() {
        assertThat(dao.getById(1).get()).isEqualTo(book1);
    }

    @Test
    @DisplayName("выбрасывать исключение при неизвестном идентификаторе")
    void givenUnknownId_whenGetById_thenNotPresent() {
        assertThat(dao.getById(99).isPresent()).isFalse();
    }

    @Test
    @DisplayName("искать книги по части названия")
    void givenExistTitlePart_whenSearchByTitlePart_thenSuccess() {
        assertThat(dao.searchByTitlePart("oo")).contains(book1, book2);
    }

    @Test
    @DisplayName("искать книгу по полному названию")
    void givenExistWholeTitle_whenSearchByTitlePart_thenSuccess() {
        assertThat(dao.searchByTitlePart("book2")).containsExactly(book2);
    }

    @Test
    @DisplayName("при поиске но несуществующей части названия возвращать пустоту")
    void givenUnknownTitlePart_whenSearchByTitlePart_thenReturnEmpty() {
        assertThat(dao.searchByTitlePart("unknown")).isEmpty();
    }

    @Test
    @DisplayName("обновлять информацию о книге")
    void whenUpdate_thenSuccess() {
        val book = new Book(1, "newBook1", new Author(2, "author2"),
                List.of(new Genre(1, "genre1"), new Genre(3, "genre3")));
        dao.update(book);
        assertThat(dao.getById(1).get()).isEqualTo(book);
    }

    @Test
    @DisplayName("добавлять информацию о книге")
    void givenNewBook_whenInsert_thenSuccess() {
        val book = new Book(0, "book4", new Author(3, "author3"),
                List.of(new Genre(2, "genre2")));
        dao.insert(book);
        int newId = books.size() + 1;
        assertThat(dao.getById(newId).get()).isEqualTo(book.copyWithNewId(newId));
    }

    @Test
    @DisplayName("удалять информацию о книге")
    void whenDelete_thenSuccess() {
        dao.delete(1);
        assertThat(dao.getById(1).isPresent()).isFalse();
        assertThat(jdbc.queryForObject(
                "select count(*) from book_genre where book_id = :id",
                Map.of("id", 1), Long.class)).isEqualTo(0L);
        assertThat(jdbc.queryForObject(
                "select count(*) from book_genre where book_id <> :id",
                Map.of("id", 1), Long.class)).isEqualTo(2L);
    }
}