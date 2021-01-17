package ru.otus.hw6.data.dao.jpa;

import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import ru.otus.hw6.data.TestData;
import ru.otus.hw6.data.dao.CommentDao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@Import(CommentDaoJpa.class)
class CommentDaoJpaTest {
    @Autowired
    private CommentDao dao;

    @Test
    void givenExistId_whenGetById_thenSuccess() {
        val comment = TestData.COMMENT_3;
        assertThat(dao.getById(comment.getId()))
                .isEqualTo(comment);
    }

    @Test
    void givenUnknownId_whenGetById_thenThrows() {
        assertThatThrownBy(() -> dao.getById(-1));
        assertThatThrownBy(() -> dao.getById(TestData.getCommentNextId()));
    }

    @Test
    void givenPersistComment_whenUpdate_thenSuccess() {
        val comment = TestData.COMMENT_4_UPDATED;
        val id = comment.getId();
        dao.update(comment);
        assertThat(dao.getById(id))
                .isEqualTo(comment);
    }

    @Test
    void givenUnmanagedComment_whenUpdate_thenThrows() {
        val comment = TestData.COMMENT_FOR_INSERT;
        assertThatThrownBy(() -> dao.update(comment));
    }

    @Test
    void givenUnmanagedComment_whenInsert_thenSuccess() {
        val comment = TestData.COMMENT_FOR_INSERT;
        assertThat(dao.insert(comment))
                .isEqualTo(dao.getById(comment.getId()));
        assertThat(comment.getId())
                .isEqualTo(TestData.getCommentNextId());
    }

    @Test
    void givenManagedComment_whenInsert_thenThrows() {
        val comment = TestData.COMMENT_2;
        assertThatThrownBy(() -> dao.insert(comment));
    }

    @Test
    void givenManagedComment_whenDelete_thenSuccess() {
        val comment = dao.getById(TestData.COMMENT_FOR_DELETE.getId());
        dao.delete(comment);
        assertThatThrownBy(() -> dao.getById(TestData.COMMENT_FOR_DELETE.getId()));
    }

    @Test
    void givenUnmanagedComment_whenDelete_thenThrows() {
        assertThatThrownBy(() -> dao.delete(TestData.COMMENT_FOR_INSERT));
    }
}