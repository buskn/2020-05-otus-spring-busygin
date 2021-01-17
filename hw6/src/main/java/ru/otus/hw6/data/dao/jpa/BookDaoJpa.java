package ru.otus.hw6.data.dao.jpa;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;
import ru.otus.hw6.data.dao.BookDao;
import ru.otus.hw6.data.model.Book;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;

@Repository
public class BookDaoJpa implements BookDao {
    @PersistenceContext
    private EntityManager em;

    @Override
    public List<Book> getAll() {
        return em.createQuery("select b from Book b", Book.class)
                .getResultList();
    }

    @Override
    public Optional<Book> getById(long id) {
        try {
            return Optional.of(
                    em.createQuery("select b from Book b where b.id = :id", Book.class)
                            .setParameter("id", id)
                            .getSingleResult());
        }
        catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Book> searchByTitlePart(String title) {
        return em.createQuery("select b from Book b where lower(b.title) like :title", Book.class)
                .setParameter("title", "%" + title.toLowerCase() + "%")
                .getResultList();
    }

    @Override
    public Book save(Book book) {
        if (book.getId() == 0) {
            em.persist(book);
            return book;
        }
        else {
            return em.merge(book);
        }
    }

    @Override
    public void delete(Book book) {
        em.remove(book);
    }
}
