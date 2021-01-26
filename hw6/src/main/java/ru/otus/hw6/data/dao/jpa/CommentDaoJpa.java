package ru.otus.hw6.data.dao.jpa;

import org.springframework.stereotype.Repository;
import ru.otus.hw6.common.HwDevelopException;
import ru.otus.hw6.data.dao.CommentDao;
import ru.otus.hw6.data.model.Book;
import ru.otus.hw6.data.model.Comment;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class CommentDaoJpa implements CommentDao {
    @PersistenceContext
    private EntityManager em;

    @Override
    public Comment getById(long id) {
        return em.createQuery("select c from Comment c where c.id = :id", Comment.class)
                .setParameter("id", id)
                .getSingleResult();
    }

    @Override
    public List<Comment> getAllByBook(Book book) {
        return em.createQuery("select c from Comment c where c.book.id = :book_id", Comment.class)
                .setParameter("book_id", book.getId())
                .getResultList();
    }

    @Override
    public void save(Comment comment) {
        assert comment.getId() >= 0;
        if (comment.getId() == 0)
            em.persist(comment);
        else
            em.merge(comment);
    }

    @Override
    public void delete(Comment comment) {
        if (comment.getId() != 0) {
            em.remove(comment);
        }
        else throw new HwDevelopException("unmanaged entity deletion: " + comment);
    }
}
