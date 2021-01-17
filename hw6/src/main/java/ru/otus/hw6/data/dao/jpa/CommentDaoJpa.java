package ru.otus.hw6.data.dao.jpa;

import org.springframework.stereotype.Repository;
import ru.otus.hw6.HwException;
import ru.otus.hw6.data.dao.CommentDao;
import ru.otus.hw6.data.model.Comment;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

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
    public void update(Comment comment) {
        if (comment.getId() > 0) {
            em.merge(comment);
        }
        else throw new HwException("entity isn't managed: " + comment);
    }

    @Override
    public Comment insert(Comment comment) {
        if (comment.getId() == 0) {
            em.persist(comment);
            return comment;
        }
        else throw new HwException("entity already persist: comment");
    }

    @Override
    public void delete(Comment comment) {
        if (comment.getId() != 0) {
            em.remove(comment);
        }
        else throw new HwException("unmanaged entity deletion: " + comment);
    }
}
