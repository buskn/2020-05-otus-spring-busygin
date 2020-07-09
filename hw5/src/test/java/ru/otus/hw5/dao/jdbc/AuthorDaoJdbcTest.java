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

    Author author1 = new Author(1, "author1");
    Author author2 = new Author(2, "author2");
    Author author3 = new Author(3, "author3");


    @Test
    @DisplayName("возвращать все экземпляры")
    void whenGetAll_thenSuccess() {
        assertThat(dao.getAll()).contains(author1, author2, author3);
    }

    @Test
    @DisplayName("возвращать экземпляр по существующему идентификатору")
    void givenExistId_whenGetById_thenSuccess() {
        assertThat(dao.getById(1)).isEqualTo(author1);
    }

    @Test
    @DisplayName("выкидывать исключение по отсутствующему идентификатору")
    void givenUnknownId_whenGetById_thenThrows() {
        assertThatExceptionOfType(DataAccessException.class)
            .isThrownBy(() -> dao.getById(99));
    }

    @Test
    @DisplayName("искать по существующему полному имени")
    void givenExistName_whenGetByName_thenSuccess() {
        assertThat(dao.getByName("author1").get()).isEqualTo(author1);
    }

    @Test
    @DisplayName("ничего не возвращать при поиске по не существующему полному имени")
    void givenUnknownName_whenGetByName_thenEmpty() {
        assertThat(dao.getByName("unknown").isPresent()).isFalse();
    }

    @Test
    @DisplayName("искать авторов по части имени")
    void givenExistNamePart_whenSearchByNamePart_thenSuccess() {
        assertThat(dao.searchByNamePart("utho")).contains(author1, author2, author3);
    }

    @Test
    @DisplayName("искать автора по целому имени")
    void givenExistWholeName_whenSearchByNamePart_thenSuccess() {
        assertThat(dao.searchByNamePart("author2")).containsExactly(author2);
    }

    @Test
    @DisplayName("не возвращать ничего при поиске по несуществующей части имени")
    void givenUnknownNamePart_whenSearchByNamePart_thenReturnEmpty() {
        assertThat(dao.searchByNamePart("unknown")).isEmpty();
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