package ru.otus.hw6.data.dao.jpa;

import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
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

    private TestData data;

    @BeforeEach
    void setUp() {
        data = new TestData();
    }

    @Test
    void whenGetAll_ThenSuccess() {
        assertThat(dao.getAll())
                .isNotNull()
                .containsExactlyInAnyOrderElementsOf(data.AUTHORS);
    }

    @Test
    void givenExistName_whenGetByName_ThenSuccess() {
        assertThat(dao.getByName(data.AUTHOR_1.getName()))
                .isPresent()
                .get()
                .isEqualTo(data.AUTHOR_1);
    }

    @Test
    void givenUnknownName_whenGetByName_ThenEmpty() {
        assertThat(dao.getByName("Unknown name"))
                .isNotPresent();
    }

    @Test
    void givenEverybodyContainsNamePart_whenSearchByNamePart_thenSuccess() {
        assertThat(dao.searchByNamePart("utho"))
                .containsExactlyInAnyOrderElementsOf(data.AUTHORS);
    }

    @Test
    void givenExistNamePart_whenSearchByNamePart_thenSuccess() {
        assertThat(dao.searchByNamePart("another"))
                .containsExactlyInAnyOrderElementsOf(List.of(data.AUTHOR_5, data.AUTHOR_6));
    }

    @Test
    void givenUnknownNamePart_whenSearchByNamePart_thenEmpty() {
        assertThat(dao.searchByNamePart("unknown")).isEmpty();
    }

    @Test
    void givenExistId_whenGetById_thenSuccess() {
        assertThat(dao.getById(data.AUTHOR_3.getId()))
                .isEqualTo(data.AUTHOR_3);
    }

    @Test
    void givenUnknownId_whenGetById_thenThrows() {
        assertThatThrownBy(() -> dao.getById(-1));
    }

    @Test
    void givenPersistAuthor_whenUpdate_thenSuccess() {
        dao.update(data.AUTHOR_6_UPDATED);
        assertThat(dao.getById(data.AUTHOR_6.getId()))
                .isEqualTo(data.AUTHOR_6_UPDATED);
    }

    @Test
    @DirtiesContext
    void givenUnmanagedAuthor_whenUpdate_thenSuccess() {
        assertThatThrownBy(() -> dao.update(data.AUTHOR_UNKNOWN));
    }

    @Test
    @DirtiesContext
    void givenUnmanagedAuthor_whenInsert_thenSuccess() {
        val authorWithId = dao.insert(data.AUTHOR_FOR_INSERT);
        assertThat(authorWithId)
                .isEqualToIgnoringGivenFields(data.AUTHOR_FOR_INSERT, "id")
                .hasFieldOrPropertyWithValue("id", data.getAuthorNextId());
    }

    @Test
    void givenPersistAuthor_whenInsert_thenSuccess() {
        assertThatThrownBy(() -> dao.insert(data.AUTHOR_2));
    }

    @Test
    void givenPersistAuthor_whenDelete_thenSuccess() {
        var author = dao.getById(data.AUTHOR_FOR_DELETE.getId());
        dao.delete(author);
        assertThatThrownBy(() -> dao.getById(data.AUTHOR_FOR_DELETE.getId()));
    }
}