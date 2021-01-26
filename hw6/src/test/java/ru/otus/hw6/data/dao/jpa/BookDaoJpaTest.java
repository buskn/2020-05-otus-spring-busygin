package ru.otus.hw6.data.dao.jpa;

import lombok.val;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import ru.otus.hw6.data.TestData;
import ru.otus.hw6.data.dao.BookDao;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@Import({BookDaoJpa.class, JpaStatistics.class})
class BookDaoJpaTest {
    @Autowired
    private BookDao dao;

    private TestData data;

    @Autowired
    private JpaStatistics statistics;

    @BeforeEach
    void setUp() {
        data = new TestData();
        statistics.startSession();
    }

    @AfterEach
    void tearDown() {
        statistics.briefAndEndSession();
    }

    @Test
    void whenGetAll_thenSuccess() {
        val result = dao.getAll();
        assertThat(data.BOOKS)
                .containsExactlyInAnyOrderElementsOf(result);
    }

    @Test
    void givenExistsId_whenGetById_thenSuccess() {
        val book = data.BOOK_1;
        val selectedOptionalBook = dao.getById(book.getId());
        assertThat(selectedOptionalBook)
                .isPresent();
        assertThat(book)
                .isEqualTo(selectedOptionalBook.get());
    }

    @Test
    void givenUnknownId_whenGetById_thenEmpty() {
        assertThat(dao.getById(data.BOOK_FOR_INSERT.getId()))
                .isNotPresent();
    }

    @Test
    void givenExistTitlePart_whenSearchByTitlePart_thenSuccess() {
        val part = "some";
        assertThat(List.of(data.BOOK_2, data.BOOK_3))
                .containsExactlyInAnyOrderElementsOf(dao.searchByTitlePart(part));
    }

    @Test
    void givenUnmanagedBook_whenSave_thenInsert() {
        val book = data.BOOK_FOR_INSERT;
        val result = dao.save(book);
        assertThat(book)
                .isEqualToIgnoringGivenFields(result, "id");
        assertThat(result.getId())
                .isEqualTo(data.getBookNextId());
        assertThat(dao.getById(data.getBookNextId()))
                .get()
                .isEqualTo(result);
    }

    @Test
    void givenManagedBook_whenSave_thenUpdate() {
        val bookBeforeUpdate = dao.getById(data.BOOK_3.getId()).get();
        val bookAfterUpdate = data.BOOK_3_UPDATED;
        val result = dao.save(bookAfterUpdate);
        assertThat(bookAfterUpdate)
                .isEqualTo(result)
                .isEqualTo(dao.getById(bookBeforeUpdate.getId()).get());
    }

    @Test
    void givenExistBook_whenDelete_thenSuccess() {
        val book = dao.getById(data.BOOK_FOR_DELETE.getId()).get();
        dao.delete(book);
        assertThat(dao.getById(book.getId()))
                .isNotPresent();
    }

    @Test
    void givenUnknownBook_whenDelete_thenSuccess() {
        val book = data.BOOK_FOR_INSERT;
        assertThatThrownBy(() -> dao.delete(book));
    }
}