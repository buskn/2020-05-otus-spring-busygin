package ru.otus.hw6.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.otus.hw6.HwException;
import ru.otus.hw6.data.dao.BookDao;
import ru.otus.hw6.data.model.Book;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BookService {
    private final BookDao dao;

    public List<Book> getAll() {
        return dao.getAll();
    }

    public Optional<Book> getById(long id) {
        return dao.getById(id);
    }

    public List<Book> searchByTitlePart(String title) {
        return dao.searchByTitlePart(title);
    }

    public Book save(Book book) {
        return dao.save(book);
    }

    public void delete(long id) {
        dao.getById(id).ifPresentOrElse(
                dao::delete,
                () -> { throw new HwException("no book with that id: " + id); });
    }
}
