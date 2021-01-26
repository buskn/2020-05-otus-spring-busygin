package ru.otus.hw6.data.dao;

import ru.otus.hw6.data.model.Book;
import ru.otus.hw6.data.model.Comment;

import java.util.List;

public interface CommentDao {
    Comment getById(long id);
    List<Comment> getAllByBook(Book book);
    void save(Comment comment);
    void delete(Comment comment);
}
