package ru.otus.hw5.dao.jdbc;

import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.otus.hw5.dao.*;

import java.util.*;

@Repository
@RequiredArgsConstructor
public class BookDaoJdbc implements BookDao {
    private final NamedParameterJdbcOperations jdbc;
    private final AuthorDao authorDao;
    private final GenreDao genreDao;

    private AuthorDao getAuthorDao() { return authorDao; }
    private GenreDao getGenreDao() { return genreDao; }

    RowMapper<Book> mapper = (rs, rowNum) -> {
        List<Genre> genres = getGenreDao().getAllByBookId(rs.getLong("id"));
        Author author = getAuthorDao().getById(rs.getLong("author_id"));
        return new Book(rs.getLong("id"),
                rs.getString("title"),
                author,
                genres);
    };

    @Override
    public List<Book> getAll() {
        return jdbc.query("select id, title, author_id from books", mapper);
    }

    @Override
    public Book getById(long id) {
        return jdbc.queryForObject(
                "select id, title, author_id from books where id = :id",
                Map.of("id", id),
                mapper);
    }

    @Override
    public void update(Book book) {
        jdbc.update("update books set title = :title, author_id = :author_id",
                Map.of("title", book.getTitle(), "author_id", book.getAuthor().getId()));
        val genresCurrent = genreDao.getAllByBookId(book.getId());
        addGenres(book.getId(), book.getGenres(), genresCurrent);
        removeGenres(book.getId(), book.getGenres(), genresCurrent);
    }

    private void addGenres(long bookId, List<Genre> newGenres, List<Genre> oldGenres) {
        val genresToAdd = new ArrayList<>(newGenres);
        genresToAdd.removeAll(oldGenres);
        genresToAdd.forEach( genre -> jdbc.update(
                "insert into book_genre(book_id, genre_id) values (:book_id, :genre_id)",
                Map.of("book_id", bookId, "genre_id", genre.getId())
        ));
    }

    private void removeGenres(long bookId, List<Genre> newGenres, List<Genre> oldGenres) {
        val genresToDelete = new ArrayList<>(oldGenres);
        genresToDelete.removeAll(newGenres);
        genresToDelete.forEach( genre -> jdbc.update(
                "delete from book_genre where book_id = :book_id and genre_id = :genre_id",
                Map.of("book_id", bookId, "genre_id", genre.getId())
        ));
    }

    @Override
    public Book insert(Book book) {
        KeyHolder key = new GeneratedKeyHolder();
        jdbc.update("insert into books(title, author_id) values (:title, :author_id)",
                new MapSqlParameterSource(Map.of(
                        "title", book.getTitle(),
                        "author_id", book.getAuthor().getId())),
                key);
        long newId = Objects.requireNonNull(key.getKey()).longValue();
        addGenres(newId, book.getGenres(), Collections.emptyList());
        return book.copyWithNewId(newId);
    }

    @Override
    public void delete(long id) {
        jdbc.update("delete from book_genre where book_id = :book_id",
                Map.of("book_id", id));
        jdbc.update("delete from books where id = :id",
                Map.of("id", id));
    }
}
