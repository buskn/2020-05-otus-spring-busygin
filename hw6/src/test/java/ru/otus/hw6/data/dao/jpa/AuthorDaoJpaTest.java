package ru.otus.hw6.data.dao.jpa;

import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import ru.otus.hw6.data.TestData;
import ru.otus.hw6.data.model.Author;

import javax.persistence.PersistenceContext;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import(AuthorDaoJpa.class)
class AuthorDaoJpaTest {
    @Autowired
    private TestEntityManager em;

    @Autowired
    private AuthorDaoJpa dao;

    @Test
    void whenGetAll_ThenSuccess() {
        assertThat(dao.getAll()).isNotNull().containsExactlyInAnyOrderElementsOf(TestData.AUTHORS);
    }

    @Test
    void givenExistName_whenGetByName_ThenSuccess() {
        assertThat(dao.getByName(TestData.AUTHOR_1.getName()))
                .isPresent()
                .get()
                .isEqualTo(TestData.AUTHOR_1);
    }

    @Test
    void givenUnknownName_whenGetByName_ThenEmpty() {
        assertThat(dao.getByName("Unknown name"))
                .isNotPresent();
    }

    @Test
    void givenEverybodyContainsNamePart_whenSearchByNamePart_thenSuccess() {
        assertThat(dao.searchByNamePart("utho"))
                .containsExactlyInAnyOrderElementsOf(TestData.AUTHORS);
    }

    @Test
    void givenExistNamePart_whenSearchByNamePart_thenSuccess() {
        assertThat(dao.searchByNamePart("another"))
                .containsExactlyInAnyOrderElementsOf(List.of(TestData.AUTHOR_5, TestData.AUTHOR_6));
    }

    @Test
    void givenUnknownNamePart_whenSearchByNamePart_thenEmpty() {
        assertThat(dao.searchByNamePart("unknown")).isEmpty();
    }

    @Test
    void givenExistId_whenGetById_thenSuccess() {
        assertThat(dao.getById(TestData.AUTHOR_3.getId()))
                .isEqualTo(TestData.AUTHOR_3);
    }

    @Test
    void givenUnknownId_whenGetById_thenThrows() {
        assertThatThrownBy(() -> dao.getById(-1));
    }

    @Test
    void givenPersistAuthor_whenUpdate_thenSuccess() {
        dao.update(TestData.AUTHOR_6_UPDATED);
        assertThat(dao.getById(TestData.AUTHOR_6.getId()))
                .isEqualTo(TestData.AUTHOR_6_UPDATED);
    }

    @Test
    @DirtiesContext
    void givenUnmanagedAuthor_whenUpdate_thenSuccess() {
        assertThatThrownBy(() -> dao.update(TestData.AUTHOR_UNKNOWN));
    }

    @Test
    @DirtiesContext
    void givenUnmanagedAuthor_whenInsert_thenSuccess() {
        val authorWithId = dao.insert(TestData.AUTHOR_FOR_INSERT);
        assertThat(authorWithId)
                .isEqualToIgnoringGivenFields(TestData.AUTHOR_FOR_INSERT, "id")
                .hasFieldOrPropertyWithValue("id", TestData.getAuthorNextId());
    }

    @Test
    void givenPersistAuthor_whenInsert_thenSuccess() {
        assertThatThrownBy(() -> dao.insert(TestData.AUTHOR_2));
    }

    @Test
    void givenPersistAuthor_whenDelete_thenSuccess() {
        var author = dao.getById(TestData.AUTHOR_FOR_DELETE.getId());
        dao.delete(author);
        assertThatThrownBy(() -> dao.getById(TestData.AUTHOR_FOR_DELETE.getId()));
    }
}