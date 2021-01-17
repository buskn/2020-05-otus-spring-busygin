package ru.otus.hw6.services;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import ru.otus.hw6.data.TestData;
import ru.otus.hw6.data.dao.AuthorDao;

import java.util.Random;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@Import(AuthorService.class)
class AuthorServiceTest {
    private final Random rand = new Random();

    @Configuration
    static class Config {}

    @MockBean
    AuthorDao dao;

    @Autowired
    AuthorService service;

    @Test
    void getAll() {
        service.getAll();
        verify(dao).getAll();
    }

    @Test
    void getByName() {
        var name = "name" + Math.random();
        service.getByName(name);
        verify(dao).getByName(name);
    }

    @Test
    void searchByNamePart() {
        var part = "part" + Math.random();
        service.searchByNamePart(part);
        verify(dao).searchByNamePart(part);
    }

    @Test
    void getById() {
        var id = rand.nextInt(1000);
        service.getById(id);
        verify(dao).getById(id);
    }

    @Test
    void update() {
        var author = TestData.AUTHOR_6_UPDATED;
        service.update(author);
        verify(dao).update(author);
    }

    @Test
    void insert() {
        var author = TestData.AUTHOR_FOR_INSERT;
        service.insert(author);
        verify(dao).insert(author);
    }

    @Test
    void delete() {
        var id = rand.nextInt(1000);
        when(dao.getById(id)).thenReturn(TestData.AUTHOR_FOR_DELETE);

        service.delete(id);

        verify(dao).getById(id);
        verify(dao).delete(TestData.AUTHOR_FOR_DELETE);
    }
}