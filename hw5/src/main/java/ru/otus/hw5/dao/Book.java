package ru.otus.hw5.dao;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import ru.otus.hw5.HwException;

import java.util.*;

@Data
public class Book {
    private final long id;
    private final String title;
    private final Author author;
    private final List<Genre> genres;

    public Book copyWithNewId(long id) {
        return new Book(id, title, author, genres);
    }

    public static class Builder {
        @Getter @Setter
        private long id;
        private String title;
        private Author author;
        private Set<Genre> genres = new HashSet<>();

        public boolean ready() {
            return title != null && !"".equals(title) && author != null;
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

        public static class BookBuilderException extends HwException {
            public BookBuilderException(String message) {
                super(message);
            }
        }

        public Book build() {
            if ( !ready() )
                throw new BookBuilderException("builder isn't ready");
            return new Book(id, title, author, new ArrayList<>(genres));
        }
    }
}
