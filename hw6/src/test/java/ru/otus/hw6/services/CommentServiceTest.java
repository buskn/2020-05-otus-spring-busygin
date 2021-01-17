package ru.otus.hw6.services;

import lombok.val;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import ru.otus.hw6.data.TestData;
import ru.otus.hw6.data.dao.CommentDao;

import java.util.Random;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
class CommentServiceTest {
    private Random random = new Random();

    @Configuration
    @Import(CommentService.class)
    static class Config {}

    @Autowired
    private CommentService service;

    @MockBean
    private CommentDao dao;

    @Test
    void getById() {
        val id = random.nextInt();
        service.getById(id);
        verify(dao).getById(id);
    }

    @Test
    void update() {
        val comment = TestData.COMMENT_4_UPDATED;
        service.update(comment);
        verify(dao).update(comment);
    }

    @Test
    void insert() {
        val comment = TestData.COMMENT_FOR_INSERT;
        service.insert(comment);
        verify(dao).insert(comment);
    }

    @Test
    void deleteById() {
        val comment = TestData.COMMENT_FOR_DELETE;
        val id = comment.getId();
        when(dao.getById(id)).thenReturn(comment);
        service.deleteById(id);
        verify(dao).getById(id);
        verify(dao).delete(comment);
    }
}