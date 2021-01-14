package ru.otus.hw6.dao.jdbc;

import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.otus.hw6.dao.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Repository
@RequiredArgsConstructor
public class BookDaoJdbc implements BookDao {
    private final NamedParameterJdbcOperations jdbc;
    private final AuthorDao authorDao;
    private final GenreDao genreDao;

    private AuthorDao getAuthorDao() { return authorDao; }
    private GenreDao getGenreDao() { return genreDao; }

    RowMapper<Book> mapperSingleBook = (rs, rowNum) -> {
        List<Genre> genres = getGenreDao().getAllByBookId(rs.getLong("id"));
        Author author = getAuthorDao().getById(rs.getLong("author_id"));
        return new Book(rs.getLong("id"),
                rs.getString("title"),
                author,
                genres);
    };

//    @Override
    public List<Book> getAll_ver0() {
        return jdbc.query("select id, title, author_id from books", mapperSingleBook);
    }

    private static class AllBooksResultSetExtractor implements ResultSetExtractor<List<Book>> {
        @Override
        public List<Book> extractData(ResultSet rs) throws SQLException, DataAccessException {
            List<Book> books = new ArrayList<>();
            Book.Builder builder = new Book.Builder();

            while (rs.next()) {
                long bookId = rs.getLong("book_id");
                if (builder.getId() != bookId) {
                    if (builder.getId() != 0) {
                        books.add(builder.build());
                    }

                    builder = new Book.Builder();

                    builder.setId(bookId);
                    builder.setTitle(rs.getString("book_title"));
                    builder.setAuthor(new Author(rs.getLong("author_id"),
                            rs.getString("author_name")));
                }

                long genreId = rs.getLong("genre_id");
                if (genreId != 0) {
                    builder.addGenre(new Genre(genreId, rs.getString("genre")));
                }
            }

            if (builder.getId() != 0) {
                books.add(builder.build());
            }

            return books;
        }
    }

//    @Override
    public List<Book> getAll_ver1() {
        return jdbc.query(
            "select b.id book_id, b.title book_title, b.author_id author_id, " +
                    "a.name author_name, g.id genre_id, g.genre genre " +
                "from books b " +
                    "join authors a on a.id = b.author_id " +
                    "left join book_genre bg on bg.book_id = b.id " +
                    "left join genres g on g.id = bg.genre_id " +
                "order by b.title, a.name, g.genre",
            new AllBooksResultSetExtractor());
    }

    @Override
    public List<Book> getAll() {
        return jdbc.query(
                "select b.id book_id, b.title book_title, a.id author_id, " +
                        "a.name author_name, " +
                        "group_concat(g.id order by g.id separator '|') genre_id_line, " +
                        "group_concat(g.genre order by g.id separator '|') genre_line " +
                    "from books b " +
                        "left join authors a on a.id = b.author_id " +
                        "left join book_genre bg on bg.book_id = b.id " +
                        "left join genres g on bg.genre_id = g.id " +
                    "group by b.id, b.title, a.id, a.name " +
                    "order by b.title, a.name ",
                (rs, num) -> {
                    val genres = new ArrayList<Genre>();
                    String genreIdLine = rs.getString("genre_id_line");
                    String genreLine = rs.getString("genre_line");
                    if (genreIdLine != null && !genreIdLine.isEmpty()) {
                        String[] genreId = genreIdLine.split("\\|");
                        String[] genre = genreLine.split("\\|");
                        for (int i = 0; i < genreId.length; i++) {
                            genres.add(new Genre(Long.parseLong(genreId[i]), genre[i]));
                        }
                    }
                    return new Book(
                            rs.getLong("book_id"),
                            rs.getString("book_title"),
                            new Author(rs.getLong("author_id"),
                                    rs.getString("author_name")),
                            genres);
                });
    }

    @Override
    public Optional<Book> getById(long id) {
        return jdbc.query(
                "select id, title, author_id from books where id = :id",
                Map.of("id", id),
                mapperSingleBook).stream().findFirst();
    }

    @Override
    public List<Book> searchByTitlePart(String title) {
        return jdbc.query("select id, title, author_id from books where lower(title) like :title",
                Map.of("title", "%" + title.trim().toLowerCase() +"%"),
                mapperSingleBook);
    }

    @Override
    public Book save(Book book) {
        if (book.getId() == 0) {
            return insert(book);
        }
        else {
            return update(book);
        }
    }

    public Book update(Book book) {
        jdbc.update("update books set title = :title, author_id = :author_id where id = :id",
                Map.of("title", book.getTitle(),
                        "author_id", book.getAuthor().getId(),
                        "id", book.getId()));
        val genresCurrent = genreDao.getAllByBookId(book.getId());
        addGenres(book.getId(), book.getGenres(), genresCurrent);
        removeGenres(book.getId(), book.getGenres(), genresCurrent);
        return getById(book.getId()).orElseThrow();
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
