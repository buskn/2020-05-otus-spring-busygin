package ru.otus.hw6.dao.jdbc;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.otus.hw6.dao.Genre;
import ru.otus.hw6.dao.GenreDao;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class GenreDaoJdbc implements GenreDao {
    private final NamedParameterJdbcOperations jdbc;

    private final RowMapper<Genre> mapper =
            (rs, rowNum) -> new Genre(rs.getLong("id"), rs.getString("genre"));

    @Override
    public void delete(long id) {
        jdbc.update("delete from genres where id = :id",
                Map.of("id", id));
    }

    @Override
    public Genre insert(Genre genre) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update("insert into genres(genre) values (:genre)",
                new MapSqlParameterSource().addValue("genre", genre.getGenre()),
                keyHolder);
        return new Genre(
                Objects.requireNonNull(keyHolder.getKey()).longValue(),
                genre.getGenre());
    }

    @Override
    public void update(Genre genre) {
        jdbc.update("update genres set genre = :genre where id = :id",
                Map.of("genre", genre.getGenre(),
                        "id", genre.getId()));
    }

    @Override
    public Genre getById(long id) {
        return jdbc.queryForObject(
                "select id, genre from genres where id = :id",
                Map.of("id", id),
                mapper);
    }

    @Override
    public Optional<Genre> getByGenre(String genre) {
        return jdbc.query("select id, genre from genres where lower(genre) = :genre",
                Map.of("genre", genre.trim().toLowerCase()),
                mapper).stream().findFirst();
    }

    @Override
    public List<Genre> searchByGenrePart(String part) {
        return jdbc.query("select id, genre from genres where lower(genre) like :genre",
                Map.of("genre", "%" + part.trim().toLowerCase() + "%"),
                mapper);
    }

    @Override
    public List<Genre> getAllByBookId(long bookId) {
        return jdbc.query(
                "select id, genre from genres g join book_genre bg on g.id = bg.genre_id" +
                        " where bg.book_id = :id",
                Map.of("id", bookId),
                mapper);
    }

    @Override
    public List<Genre> getAll() {
        return jdbc.query("select id, genre from genres", mapper);
    }
}
