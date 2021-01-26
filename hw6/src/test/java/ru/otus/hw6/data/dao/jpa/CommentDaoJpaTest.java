package ru.otus.hw6.data.dao.jpa;

import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import ru.otus.hw6.data.TestData;
import ru.otus.hw6.data.dao.CommentDao;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@Import(CommentDaoJpa.class)
class CommentDaoJpaTest {
    @Autowired
    private CommentDao dao;

    private TestData data;

    @BeforeEach
    void setUp() {
        data = new TestData();
    }

    @Test
    void givenExistId_whenGetById_thenSuccess() {
        val comment = data.COMMENT_3;
        assertThat(comment)
                .isEqualTo(dao.getById(comment.getId()));
    }

    @Test
    void givenUnknownId_whenGetById_thenThrows() {
        assertThatThrownBy(() -> dao.getById(-1));
        assertThatThrownBy(() -> dao.getById(data.getCommentNextId()));
    }

    @Test
    void givenExistBookWithComments_whenGetAllByBook_thenSuccess() {
        assertThat(List.of(data.COMMENT_1, data.COMMENT_2, data.COMMENT_3))
                .containsExactlyInAnyOrderElementsOf(dao.getAllByBook(data.BOOK_1));
    }

    @Test
    void givenPersistComment_whenSave_thenUpdate() {
        val comment = data.COMMENT_4_UPDATED;
        val id = comment.getId();
        dao.save(comment);
        assertThat(comment)
                .isEqualTo(dao.getById(id));
    }

    @Test
    void givenUnmanagedComment_whenSave_thenInsert() {
        val comment = data.COMMENT_FOR_INSERT;
        dao.save(comment);
        assertThat(comment.getId())
                .isEqualTo(data.getCommentNextId());
    }

    @Test
    void givenManagedComment_whenDelete_thenSuccess() {
        val comment = dao.getById(data.COMMENT_FOR_DELETE.getId());
        dao.delete(comment);
        assertThatThrownBy(() -> dao.getById(data.COMMENT_FOR_DELETE.getId()));
    }

    @Test
    void givenUnmanagedComment_whenDelete_thenThrows() {
        assertThatThrownBy(() -> dao.delete(data.COMMENT_FOR_INSERT));
    }
}