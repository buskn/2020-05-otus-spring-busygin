package ru.otus.hw6.data.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import ru.otus.hw6.HwException;

import javax.persistence.*;
import java.util.*;

@Entity
@Table(name = "books")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column
    private String title;

    @OneToOne
    @JoinColumn(name = "author_id")
    private Author author;

    @ManyToMany
    @JoinTable(name = "book_genre",
            joinColumns = @JoinColumn(name = "book_id"),
            inverseJoinColumns = @JoinColumn(name = "genre_id"))
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<Genre> genres;

    @OneToMany
    @JoinColumn(name = "book_id")
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<Comment> comments;

    public Book copyWithNewId(long id) {
        return new Book(id, title, author, genres, comments);
    }

    public static class Builder {
        private long id;
        private String title;
        private Author author;
        private Set<Genre> genres = new HashSet<>();
        private Set<Comment> comments = new HashSet<>();

        public boolean ready() {
            return title != null && !"".equals(title) && author != null;
        }

        public Builder setId(long id) {
            this.id = id;
            return this;
        }

        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        public Builder setAuthor(Author author) {
            this.author = author;
            return this;
        }

        public Optional<String> getTitle() {
            return Optional.ofNullable(title);
        }

        public Set<Genre> getGenres() {
            return Collections.unmodifiableSet(genres);
        }

        public Optional<Author> getAuthor() {
            return Optional.ofNullable(author);
        }

        public Builder setGenres(Collection<Genre> genres) {
            this.genres = new HashSet<>(genres);
            return this;
        }

        public Builder addGenre(Genre genre) {
            genres.add(genre);
            return this;
        }

        public Builder removeGenre(Genre genre) {
            genres.remove(genre);
            return this;
        }

        public Builder setComments(Collection<Comment> comments) {
            this.comments = new HashSet<>(comments);
            return this;
        }

        public Builder addComment(Comment comment) {
            comments.add(comment);
            return this;
        }

        public Builder removeComment(Comment comment) {
            comments.remove(comment);
            return this;
        }

        public static class BookBuilderException extends HwException {
            public BookBuilderException(String message) {
                super(message);
            }
        }

        public Book build() {
            if (!ready())
                throw new BookBuilderException("builder isn't ready");
            return new Book(id, title, author, new ArrayList<>(genres), new ArrayList<>(comments));
        }
    }
}
