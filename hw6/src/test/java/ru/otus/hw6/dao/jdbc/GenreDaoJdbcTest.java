package ru.otus.hw6.dao.jdbc;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataAccessException;
import org.springframework.test.annotation.DirtiesContext;
import ru.otus.hw6.dao.Genre;
import ru.otus.hw6.dao.GenreDao;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@JdbcTest
@Import(GenreDaoJdbc.class)
@DisplayName("Класс GenreDaoJdbc должен")
class GenreDaoJdbcTest {

    @Autowired
    private GenreDao dao;

    private final Genre
        genre1 = new Genre(1, "genre1"),
        genre2 = new Genre(2, "genre2"),
        genre3 = new Genre(3, "genre3"),
        genre4 = new Genre(4, "genre4");
    private final List<Genre> allGenres = List.of(genre1, genre2, genre3, genre4);

    @Test
    @DisplayName("возвращать все экземпляры")
    void whenGetAll_thenSuccess() {
        assertThat(dao.getAll()).containsExactlyInAnyOrderElementsOf(allGenres);
    }

    @Test
    @DisplayName("возвращать экземпляр по существующему идентификатору")
    void givenExistId_whenGetById_thenSuccess() {
        assertThat(dao.getById(genre1.getId())).isEqualTo(genre1);
    }

    @Test
    @DisplayName("выкидывать исключение по отсутствующему идентификатору")
    void givenUnknownId_whenGetById_thenThrows() {
        assertThatExceptionOfType(DataAccessException.class)
                .isThrownBy(() -> dao.getById(99));
    }

    @Test
    @DisplayName("искать по полному наименованию")
    void givenExistGenre_whenGetById_thenSuccess() {
        assertThat(dao.getByGenre(genre2.getGenre()).get()).isEqualTo(genre2);
    }

    @Test
    @DisplayName("искать по полному наименованию")
    void givenUnknownGenre_whenGetById_thenEmpty() {
        assertThat(dao.getByGenre("unknown").isPresent()).isFalse();
    }

    @Test
    @DisplayName("обновлять данные о записи")
    void givenExistGenre_whenUpdate_thenSuccess() {
        Genre genre = new Genre(1, "newName");
        dao.update(genre);
        assertThat(dao.getById(genre.getId())).isEqualTo(genre);
    }

    @Test
    @DisplayName("не обновлять данные об отсутствующей записи")
    void givenNewGenre_whenUpdate_thenNothingToUpdate() {
        Genre author = new Genre(99, "newName");
        dao.update(author);
        assertThatExceptionOfType(DataAccessException.class)
                .isThrownBy(() -> dao.getById(author.getId()));
    }

    @Test
    @DisplayName("добавлять запись")
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    void givenNewGenre_whenInsert_thenSuccess() {
        Genre genre = new Genre(0, "genre5");
        assertThat(dao.insert(genre))
                .isEqualTo(new Genre(5, genre.getGenre()));
    }

    @Test
    @DisplayName("выкидывать исключение при добавлении дублирующего названия жанра")
    void givenExistNameAuthor_whenInsert_thenThrows() {
        Genre genre = new Genre(0, "genre1");
        assertThatExceptionOfType(DataAccessException.class)
                .isThrownBy(() -> dao.insert(genre));
    }

    @Test
    @DisplayName("удалять запись")
    void whenDelete_thenSuccess() {
        dao.delete(4);
        assertThatExceptionOfType(DataAccessException.class)
                .isThrownBy(() -> dao.getById(4));
    }

    @Test
    @DisplayName("искать по части названия жанра")
    void givenExistPart_whenSearchByGenrePart_thenSuccess() {
        assertThat(dao.searchByGenrePart("enre"))
                .containsExactlyInAnyOrderElementsOf(allGenres);
    }

    @Test
    @DisplayName("не возвращать ничего при поиске по не совпадающей ни с чем части")
    void givenUnknownPart_whenSearchByGenrePart_thenEmpty() {
        assertThat(dao.searchByGenrePart("unknown")).isEmpty();
    }

    @Test
    @DisplayName("возвращать все жанры определенной книги")
    void whenGetAllByBookId_thenSuccess() {
        assertThat(dao.getAllByBookId(1))
                .contains(new Genre(1, "genre1"), new Genre(2, "genre2"));
    }
}