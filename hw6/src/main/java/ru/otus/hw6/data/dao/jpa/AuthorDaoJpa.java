package ru.otus.hw6.data.dao.jpa;

import lombok.val;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;
import ru.otus.hw6.HwException;
import ru.otus.hw6.data.dao.AuthorDao;
import ru.otus.hw6.data.model.Author;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;

@Repository
public class AuthorDaoJpa implements AuthorDao {
    @PersistenceContext
    private EntityManager em;

    @Override
    public List<Author> getAll() {
        return em.createQuery("select a from Author a", Author.class).getResultList();
    }

    @Override
    public Optional<Author> getByName(String name) {
        try {
            val result = em.createQuery("select a from Author a where a.name = :name", Author.class)
                    .setParameter("name", name)
                    .getSingleResult();
            return Optional.of(result);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Author> searchByNamePart(String name) {
        return em.createQuery("select a from Author a where lower(a.name) like :name", Author.class)
                .setParameter("name", "%" + name.toLowerCase() + "%")
                .getResultList();
    }

    @Override
    public Author getById(long id) {
        return em.createQuery("select a from Author a where a.id = :id", Author.class)
                .setParameter("id", id)
                .getSingleResult();
    }

    @Override
    public void update(Author author) {
        if (author.getId() > 0) {
            em.merge(author);
        }
        else throw new HwException("entity isn't persist: " + author);
    }

    @Override
    public Author insert(Author author) {
            em.persist(author);
            return author;
    }

    @Override
    public void delete(Author author) {
        em.remove(author);
    }
}
