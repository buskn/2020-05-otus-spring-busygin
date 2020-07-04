package ru.otus.hw5.dao.jdbc;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataAccessException;
import org.springframework.test.annotation.DirtiesContext;
import ru.otus.hw5.dao.Author;
import ru.otus.hw5.dao.AuthorDao;

import static org.assertj.core.api.Assertions.*;

@JdbcTest
@Import(AuthorDaoJdbc.class)
@DisplayName("Класс AuthorDaoJdbc должен")
class AuthorDaoJdbcTest {

    @Autowired private AuthorDao dao;

    @Test
    @DisplayName("возвращать все экземпляры")
    void whenGetAll_thenSuccess() {
        assertThat(dao.getAll()).contains(
                new Author(1, "author1"),
                new Author(2, "author2"),
                new Author(3, "author3")
        );
    }

    @Test
    @DisplayName("возвращать экземпляр по существующему идентификатору")
    void givenExistId_whenGetById_thenSuccess() {
        assertThat(dao.getById(1)).isEqualTo(new Author(1, "author1"));
    }

    @Test
    @DisplayName("выкидывать исключение по отсутствующему идентификатору")
    void givenUnknownId_whenGetById_thenThrows() {
        assertThatExceptionOfType(DataAccessException.class)
            .isThrownBy(() -> dao.getById(99));
    }

    @Test
    @DisplayName("обновлять данные о записи")
    void givenExistAuthor_whenUpdate_thenSuccess() {
        Author author = new Author(1, "newName");
        dao.update(author);
        assertThat(dao.getById(author.getId())).isEqualTo(author);
    }

    @Test
    @DisplayName("не обновлять данные об отсутствующей записи")
    void givenNewAuthor_whenUpdate_thenNothingToUpdate() {
        Author author = new Author(99, "newName");
        dao.update(author);
        assertThatExceptionOfType(DataAccessException.class)
                .isThrownBy(() -> dao.getById(author.getId()));
    }

    @Test
    @DisplayName("добавлять запись")
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    void givenNewAuthor_whenInsert_thenSuccess() {
        Author author = new Author(0, "author5");
        assertThat(dao.insert(author))
                .isEqualTo(new Author(5, author.getName()));
    }

    @Test
    @DisplayName("выкидывать исключение при добавлении автора в дублирующимся именем")
    void givenExistNameAuthor_whenInsert_thenThrows() {
        Author author = new Author(0, "author1");
        assertThatExceptionOfType(DataAccessException.class)
                .isThrownBy(() -> dao.insert(author));
    }

    @Test
    @DisplayName("удалять запись")
    void whenDelete_thenSuccess() {
        dao.delete(4);
        assertThatExceptionOfType(DataAccessException.class)
                .isThrownBy(() -> dao.getById(4));
    }
}