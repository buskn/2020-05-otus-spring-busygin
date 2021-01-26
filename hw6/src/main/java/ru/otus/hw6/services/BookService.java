package ru.otus.hw6.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw6.common.HwException;
import ru.otus.hw6.data.dao.BookDao;
import ru.otus.hw6.data.dao.CommentDao;
import ru.otus.hw6.data.model.Book;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BookService {
    private final BookDao bookDao;
    private final CommentDao commentDao;

    @Transactional(readOnly = true)
    public List<Book> getAll() {
        return bookDao.getAll();
    }

    @Transactional(readOnly = true)
    public Optional<Book> getById(long id) {
        return bookDao.getById(id);
    }

    @Transactional(readOnly = true)
    public List<Book> searchByTitlePart(String title) {
        return bookDao.searchByTitlePart(title);
    }

    @Transactional
    public Book save(Book book) {
        return bookDao.save(book);
    }

    @Transactional
    public void deleteById(long id) {
        bookDao.getById(id).ifPresentOrElse(
                bookDao::delete,
                () -> { throw new HwException("no book with that id: " + id); });
    }

    public void delete(Book book) {
        deleteById(book.getId());
    }
}
