package ru.otus.hw6.data.dao.jpa;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import ru.otus.hw6.data.TestData;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@Import(GenreDaoJpa.class)
class GenreDaoJpaTest {
    @Autowired
    GenreDaoJpa dao;

    @Test
    void whenGetAll_thenSuccess() {
        assertThat(dao.getAll())
                .containsExactlyInAnyOrderElementsOf(TestData.GENRES);
    }

    @Test
    void getAllByBookId() {
        assertThatThrownBy(() -> dao.getAllByBookId(-1));
    }

    @Test
    void givenAllContainingPart_whenSearchByGenrePart_thenSuccess() {
        assertThat(dao.searchByGenrePart("genre"))
                .containsExactlyInAnyOrderElementsOf(TestData.GENRES);
    }

    @Test
    void givenExistPart_whenSearchByGenrePart_thenSuccess() {
        assertThat(dao.searchByGenrePart("another"))
                .containsExactlyInAnyOrder(TestData.GENRE_5, TestData.GENRE_6);
    }

    @Test
    void givenUnknownPart_whenSearchByGenrePart_thenEmpty() {
        assertThat(dao.searchByGenrePart("unknown"))
                .isEmpty();
    }

    @Test
    void givenExistId_whenGetById_thenSuccess() {
        assertThat(dao.getById(TestData.GENRE_3.getId()))
                .isEqualTo(TestData.GENRE_3);
    }

    @Test
    void givenUnknownId_whenGetById_thenThrows() {
        assertThatThrownBy(() -> dao.getById(TestData.getGenreNextId()));
    }

    @Test
    void givenExistGenre_whenGetByGenre_thenSuccess() {
        assertThat(dao.getByGenre(TestData.GENRE_1.getGenre().toUpperCase()))
                .isPresent().get()
                .isEqualTo(TestData.GENRE_1);
    }

    @Test
    void givenUnknownGenre_whenGetByGenre_thenEmpty() {
        assertThat(dao.getByGenre("unknown"))
                .isNotPresent();
    }

    @Test
    void givenPersistGenre_whenUpdate_thenSuccess() {
        dao.update(TestData.GENRE_6_UPDATED);
        assertThat(dao.getById(TestData.GENRE_6.getId()))
                .isEqualTo(TestData.GENRE_6_UPDATED);
    }

    @Test
    void givenTransientGenre_whenUpdate_thenThrows() {
        assertThatThrownBy(() -> dao.update(TestData.GENRE_FOR_INSERT));
    }

    @Test
    void givenUnmanagedGenre_whenInsert_thenSuccess() {
        var inserted = dao.insert(TestData.GENRE_FOR_INSERT);
        assertThat(dao.getById(inserted.getId()))
                .isEqualTo(TestData.GENRE_FOR_INSERT);
    }

    @Test
    void givenExistGenre_whenDelete_thenSuccess() {
        var genre = dao.getById(TestData.GENRE_FOR_DELETE.getId());
        dao.delete(genre);
        assertThatThrownBy(() -> dao.getById(genre.getId()));
    }

}