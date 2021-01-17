package ru.otus.hw6.data.dao.jpa;

import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import ru.otus.hw6.data.TestData;
import ru.otus.hw6.data.dao.BookDao;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import(BookDaoJpa.class)
class BookDaoJpaTest {
    @Autowired
    private BookDao dao;

    @Test
    void whenGetAll_thenSuccess() {
        val result = dao.getAll();
        assertThat(TestData.BOOKS)
                .containsExactlyInAnyOrderElementsOf(result);
    }

    @Test
    void givenExistsId_whenGetById_thenSuccess() {
        val book = TestData.BOOK_1;
        val selectedOptionalBook = dao.getById(book.getId());
        assertThat(selectedOptionalBook)
                .isPresent();
        assertThat(book)
                .isEqualTo(selectedOptionalBook.get());
    }

    @Test
    void givenUnknownId_whenGetById_thenEmpty() {
        assertThat(dao.getById(TestData.BOOK_FOR_INSERT.getId()))
                .isNotPresent();
    }

    @Test
    void givenExistTitlePart_whenSearchByTitlePart_thenSuccess() {
        val part = "some";
        assertThat(List.of(TestData.BOOK_2, TestData.BOOK_3))
                .containsExactlyInAnyOrderElementsOf(dao.searchByTitlePart(part));
    }

    @Test
    void givenUnmanagedBook_whenSave_thenInsert() {
        val book = TestData.BOOK_FOR_INSERT;
        val result = dao.save(book);
        assertThat(book)
                .isEqualToIgnoringGivenFields(result, "id");
        assertThat(result.getId())
                .isEqualTo(TestData.getBookNextId());
        assertThat(dao.getById(TestData.getBookNextId()))
                .get()
                .isEqualTo(result);
    }

    @Test
    void givenManagedBook_whenSave_thenUpdate() {
        val bookBeforeUpdate = dao.getById(TestData.BOOK_3.getId()).get();
        val bookAfterUpdate = TestData.BOOK_3_UPDATED;
        val result = dao.save(bookAfterUpdate);
        assertThat(bookAfterUpdate)
                .isEqualTo(result)
                .isEqualTo(dao.getById(bookBeforeUpdate.getId()).get());
    }

    @Test
    void givenExistBook_whenDelete_thenSuccess() {
        val book = dao.getById(TestData.BOOK_FOR_DELETE.getId()).get();
        dao.delete(book);
        assertThat(dao.getById(book.getId()))
                .isNotPresent();
    }

    @Test
    void givenUnknownBook_whenDelete_thenSuccess() {
        val book = TestData.BOOK_FOR_INSERT;
        assertThatThrownBy(() -> dao.delete(book));
    }
}