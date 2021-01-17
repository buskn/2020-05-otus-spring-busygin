package ru.otus.hw6.data.dao.jpa;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;
import ru.otus.hw6.HwException;
import ru.otus.hw6.data.dao.GenreDao;
import ru.otus.hw6.data.model.Genre;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;

@Repository
public class GenreDaoJpa implements GenreDao {
    @PersistenceContext
    private EntityManager em;

    @Override
    public List<Genre> getAll() {
        return em.createQuery("from Genre", Genre.class).getResultList();
    }

    @Override
    public List<Genre> getAllByBookId(long bookId) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<Genre> searchByGenrePart(String part) {
        return em.createQuery("select g from Genre g where lower(g.genre) like :genre", Genre.class)
                .setParameter("genre", "%" + part.toLowerCase() + "%")
                .getResultList();
    }

    @Override
    public Genre getById(long id) {
        return em.createQuery("select g from Genre g where g.id = :id", Genre.class)
                .setParameter("id", id)
                .getSingleResult();
    }

    @Override
    public Optional<Genre> getByGenre(String genre) {
        try {
            return Optional.of(
                    em.createQuery("select g from Genre g where lower(g.genre) = :genre", Genre.class)
                    .setParameter("genre", genre.toLowerCase())
                    .getSingleResult()
            );
        }
        catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public void update(Genre genre) {
        if (genre.getId() > 0) {
            em.merge(genre);
        }
        else throw new HwException("entity isn't persist: " + genre);
    }

    @Override
    public Genre insert(Genre genre) {
        if (genre.getId() == 0) {
            em.persist(genre);
            return genre;
        }
        else throw new HwException("entity is managed already: " + genre);
    }

    @Override
    public void delete(Genre genre) {
        em.remove(genre);
    }
}
