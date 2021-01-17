package ru.otus.hw6.services;

import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import ru.otus.hw6.HwException;
import ru.otus.hw6.data.TestData;
import ru.otus.hw6.data.dao.BookDao;

import java.util.Optional;
import java.util.Random;

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
    BookDao dao;

    @Test
    void getAll() {
        service.getAll();
        verify(dao).getAll();
    }

    @Test
    void getById() {
        val id = random.nextInt();
        service.getById(id);
        verify(dao).getById(id);
    }

    @Test
    void searchByTitlePart() {
        var part = "part" + random.nextInt();
        service.searchByTitlePart(part);
        verify(dao).searchByTitlePart(part);
    }

    @Test
    void save() {
        val book = TestData.BOOK_3_UPDATED;
        service.save(book);
        verify(dao).save(book);
    }

    @Test
    void givenExistsId_whenDelete_thenSuccess() {
        val book = TestData.BOOK_2;
        val id = book.getId();
        when(dao.getById(id)).thenReturn(Optional.of(book));
        service.delete(id);
        verify(dao).getById(id);
        verify(dao).delete(book);
    }

    @Test
    void givenUnknownId_whenDelete_thenThrow() {
        val id = random.nextInt(100000) + TestData.getBookNextId();
        Assertions.assertThatThrownBy(() -> service.delete(id))
                .isInstanceOf(HwException.class);
        verify(dao).getById(id);
        verify(dao, never()).delete(any());
    }
}