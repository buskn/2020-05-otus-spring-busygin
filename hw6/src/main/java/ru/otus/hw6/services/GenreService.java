package ru.otus.hw6.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw6.data.dao.GenreDao;
import ru.otus.hw6.data.model.Genre;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GenreService {
    private final GenreDao dao;

    @Transactional(readOnly = true)
    public List<Genre> getAll() {
        return dao.getAll();
    }

    @Transactional(readOnly = true)
    public List<Genre> searchByGenrePart(String part) {
        return dao.searchByGenrePart(part);
    }

    @Transactional(readOnly = true)
    public Genre getById(long id) {
        return dao.getById(id);
    }

    @Transactional(readOnly = true)
    public Optional<Genre> getByGenre(String genre) {
        return dao.getByGenre(genre);
    }

    @Transactional
    public void update(Genre genre) {
        dao.update(genre);
    }

    @Transactional
    public Genre insert(Genre genre) {
        return dao.insert(genre);
    }

    @Transactional
    public void delete(long id) {
        dao.delete(dao.getById(id));
    }
}
