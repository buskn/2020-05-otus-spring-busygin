package ru.otus.hw6.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw6.data.dao.CommentDao;
import ru.otus.hw6.data.model.Comment;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentDao dao;

    @Transactional(readOnly = true)
    public Comment getById(long id) {
        return dao.getById(id);
    }

    @Transactional
    public void update(Comment comment) {
        dao.update(comment);
    }

    @Transactional
    public Comment insert(Comment comment) {
        return dao.insert(comment);
    }

    @Transactional
    public void deleteById(long id) {
        dao.delete(dao.getById(id));
    }
}
