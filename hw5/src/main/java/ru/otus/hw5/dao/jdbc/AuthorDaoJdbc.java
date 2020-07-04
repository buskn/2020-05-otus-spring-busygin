package ru.otus.hw5.dao.jdbc;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.otus.hw5.dao.Author;
import ru.otus.hw5.dao.AuthorDao;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * DAO для авторов книг
 */
@Repository
@RequiredArgsConstructor
public class AuthorDaoJdbc implements AuthorDao {
    private final NamedParameterJdbcOperations jdbc;

    private final RowMapper<Author> mapper =
            (rs, rowNum) -> new Author(rs.getLong("id"), rs.getString("name"));

    @Override
    public void delete(long id) {
        jdbc.update("delete from authors where id = :id",
                Map.of("id", id));
    }

    @Override
    public Author insert(Author author) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update("insert into authors(name) values (:name)",
                new MapSqlParameterSource().addValue("name", author.getName()),
                keyHolder);
        return new Author(
                Objects.requireNonNull(keyHolder.getKey()).longValue(),
                author.getName());
    }

    @Override
    public void update(Author author) {
        jdbc.update("update authors set name = :name where id = :id",
                Map.of("name", author.getName(),
                        "id", author.getId()));
    }

    @Override
    public Author getById(long id) {
        return jdbc.queryForObject(
                "select id, name from authors where id = :id",
                Map.of("id", id),
                mapper);
    }

    public List<Author> getAll() {
        return jdbc.query("select id, name from authors", mapper);
    }
}
