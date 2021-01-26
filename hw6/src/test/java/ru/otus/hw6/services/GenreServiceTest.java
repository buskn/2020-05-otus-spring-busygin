package ru.otus.hw6.services;

import lombok.val;
import org.assertj.core.api.Assert;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import ru.otus.hw6.data.TestData;
import ru.otus.hw6.data.dao.GenreDao;

import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@Import(GenreService.class)
class GenreServiceTest {
    private final Random rand = new Random();

    @Configuration
    static class Config {}

    @Autowired
    private GenreService service;

    @MockBean
    private GenreDao dao;

    private TestData data;

    @BeforeEach
    void setUp() {
        data = new TestData();
    }

    @Test
    void getAll() {
        service.getAll();
        verify(dao).getAll();
    }

    @Test
    void searchByGenrePart() {
        var part = "part" + rand.nextInt();
        when(dao.searchByGenrePart(part)).thenReturn(data.GENRES);
        assertThat(service.searchByGenrePart(part))
                .containsExactlyInAnyOrderElementsOf(data.GENRES);
        verify(dao).searchByGenrePart(part);
    }

    @Test
    void getById() {
        val id = rand.nextInt();
        service.getById(id);
        verify(dao).getById(id);
    }

    @Test
    void getByGenre() {
        val genre = "genre" + rand.nextInt();
        service.getByGenre(genre);
        verify(dao).getByGenre(genre);
    }

    @Test
    void update() {
        val genre = data.GENRE_6_UPDATED;
        service.update(genre);
        verify(dao).update(genre);
    }

    @Test
    void insert() {
        val genre = data.GENRE_FOR_INSERT;
        service.insert(genre);
        verify(dao).insert(genre);
    }

    @Test
    void delete() {
        val id = data.GENRE_FOR_DELETE.getId();
        val genre = data.GENRE_FOR_DELETE;
        when(dao.getById(id)).thenReturn(genre);
        service.delete(id);
        verify(dao).getById(id);
        verify(dao).delete(genre);
    }
}