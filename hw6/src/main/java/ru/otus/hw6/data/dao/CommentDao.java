package ru.otus.hw6.data.dao;

import ru.otus.hw6.data.model.Comment;
import ru.otus.hw6.data.model.Genre;

public interface CommentDao {
    Comment getById(long id);
    void update(Comment comment);
    Comment insert(Comment comment);
    void delete(Comment comment);
}
