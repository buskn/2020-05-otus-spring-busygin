package ru.otus.hw6.services;

import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import ru.otus.hw6.common.HwException;
import ru.otus.hw6.data.TestData;
import ru.otus.hw6.data.dao.BookDao;
import ru.otus.hw6.data.dao.CommentDao;

import java.util.Optional;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@SpringBootTest
class BookServiceTest {
    private Random random = new Random();

    @Configuration
    @Import(BookService.class)
    static class Config {
    }

    @Autowired
    private BookService service;

    @MockBean
    private BookDao bookDao;

    @MockBean
    private CommentDao commentDao;

    private TestData data;

    @BeforeEach
    void setUp() {
        data = new TestData();
    }

    @Test
    void getAll() {
        service.getAll();
        verify(bookDao).getAll();
    }

    @Test
    void getById() {
        val id = random.nextInt();
        service.getById(id);
        verify(bookDao).getById(id);
    }

    @Test
    void searchByTitlePart() {
        var part = "part" + random.nextInt();
        service.searchByTitlePart(part);
        verify(bookDao).searchByTitlePart(part);
    }

    @Test
    void save() {
        val book = data.BOOK_3_UPDATED;
        service.save(book);
        verify(bookDao).save(book);
    }

    @Test
    void givenExistsId_whenDeleteById_thenSuccess() {
        val book = data.BOOK_2;
        val id = book.getId();
        when(bookDao.getById(id)).thenReturn(Optional.of(book));
        service.deleteById(id);
        verify(bookDao).getById(id);
        verify(bookDao).delete(book);
    }

    @Test
    void givenUnknownId_whenDeleteById_thenThrow() {
        val id = random.nextInt(100000) + data.getBookNextId();
        Assertions.assertThatThrownBy(() -> service.deleteById(id))
                .isInstanceOf(HwException.class);
        verify(bookDao).getById(id);
        verify(bookDao, never()).delete(any());
    }

    @Test
    void givenExistsId_whenDelete_thenSuccess() {
        val book = data.BOOK_2;
        val id = book.getId();
        when(bookDao.getById(id)).thenReturn(Optional.of(book));

        service.delete(book);

        verify(bookDao).getById(id);
        verify(bookDao).delete(book);
    }

    @Test
    void givenUnknownId_whenDelete_thenThrow() {
        val book = data.BOOK_FOR_INSERT;
        Assertions.assertThatThrownBy(() -> service.delete(book))
                .isInstanceOf(HwException.class);
        verify(bookDao, never()).delete(any());
    }
}