package ru.otus.hw6.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw6.data.dao.AuthorDao;
import ru.otus.hw6.data.model.Author;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthorService {
    private final AuthorDao dao;

    @Transactional(readOnly = true)
    public List<Author> getAll() {
        return dao.getAll();
    }

    @Transactional(readOnly = true)
    public Optional<Author> getByName(String name) {
        return dao.getByName(name);
    }

    @Transactional(readOnly = true)
    public List<Author> searchByNamePart(String name) {
        return dao.searchByNamePart(name);
    }

    @Transactional(readOnly = true)
    public Author getById(long id) {
        return dao.getById(id);
    }

    @Transactional
    public void update(Author author) {
        dao.update(author);
    }

    @Transactional
    public Author insert(Author author) {
        return dao.insert(author);
    }

    @Transactional
    public void delete(long id) {
        dao.delete(dao.getById(id));
    }
}
