package ru.otus.hw6.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw6.data.dao.CommentDao;
import ru.otus.hw6.data.model.Book;
import ru.otus.hw6.data.model.Comment;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentDao dao;

    @Transactional(readOnly = true)
    public Comment getById(long id) {
        return dao.getById(id);
    }

    @Transactional
    public List<Comment> getAllByBook(Book book) {
        return dao.getAllByBook(book);
    }

    @Transactional
    public void save(Comment comment) {
        dao.save(comment);
    }

    @Transactional
    public void deleteById(long id) {
        dao.delete(dao.getById(id));
    }

    @Transactional
    public void delete(Comment comment) {
        deleteById(comment.getId());
    }
}
