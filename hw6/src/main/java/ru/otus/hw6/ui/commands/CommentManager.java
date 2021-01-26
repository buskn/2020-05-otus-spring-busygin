package ru.otus.hw6.ui.commands;

import lombok.NoArgsConstructor;
import ru.otus.hw6.data.model.Comment;

import java.util.*;

@NoArgsConstructor
public class CommentManager {
    private final List<Comment> comments = new ArrayList<>();
    private final List<Comment> commentsToSave = new ArrayList<>();
    private final List<Comment> commentsToRemove = new ArrayList<>();

    public CommentManager(Collection<Comment> comments) {
        this.comments.addAll(comments);
    }

    public void markToRemove(Comment comment) {
        assert comments.contains(comment);
        if (commentsToSave.contains(comment)) {
            commentsToSave.remove(comment);
        }
        else {
            comments.remove(comment);
            commentsToRemove.add(comment);
        }
    }

    public void unmarkRemoval(Comment comment) {
        assert commentsToRemove.contains(comment);
        commentsToRemove.remove(comment);
        comments.add(comment);
    }

    public void addNewComment(Comment comment) {
        assert ! ( comments.contains(comment) || commentsToSave.contains(comment) || commentsToRemove.contains(comment));
        commentsToSave.add(comment);
    }

    public void updateComment(Comment comment) {
        assert ( comments.contains(comment) || commentsToSave.contains(comment) )
                && !commentsToRemove.contains(comment);
        if (comments.contains(comment)) {
            comments.remove(comment);
            commentsToSave.add(comment);
        }
    }

    public List<Comment> getComments() {
        return Collections.unmodifiableList(comments);
    }

    public List<Comment> getCommentsToSave() {
        return Collections.unmodifiableList(commentsToSave);
    }

    public List<Comment> getCommentsToRemove() {
        return Collections.unmodifiableList(commentsToRemove);
    }
}
