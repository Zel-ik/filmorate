package ru.yandex.practicum.filmorate.dao.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.DirectorDao;
import ru.yandex.practicum.filmorate.entity.film.Director;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Component
@Slf4j
@RequiredArgsConstructor
public class DirectorDaoImpl implements DirectorDao {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Optional<Director> getDirectorById(long id) {
        SqlRowSet directorRows = jdbcTemplate.queryForRowSet("SELECT * FROM \"directors\" d WHERE DIRECTOR_ID = ?", id);
        if (directorRows.next()) {
            Director director = Director.builder()
                    .id(directorRows.getLong("DIRECTOR_ID"))
                    .name(Objects.requireNonNull(directorRows.getString("NAME")).strip())
                    .build();
            log.info("Найден режиссер: {}", director);
            return Optional.of(director);
        } else {
            log.info("Режиссер с идентификатором {} не найден.", id);
            return Optional.empty();
        }
    }

    @Override
    public List<Director> getAllDirector() {
        String sql = "SELECT * FROM \"directors\" d;";
        List<Director> directorList = jdbcTemplate.query(sql, (rs, rowNum) -> Director.builder()
                .id(rs.getLong("DIRECTOR_ID"))
                .name(rs.getString("NAME").strip())
                .build());
        log.info("Список жанров получен, длина = {}", directorList.size());
        return directorList;
    }

    @Override
    public Long addDirector(Director director) {
        String sqlDirectors = "INSERT INTO \"directors\" (NAME) VALUES (?);";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(conn -> {
            PreparedStatement preparedStatement = conn.prepareStatement(sqlDirectors, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, director.getName().strip());
            return preparedStatement;
        }, keyHolder);
        Long id = Objects.requireNonNull(keyHolder.getKey()).longValue();
        log.info("Режиссер с id={} добавлен", id);
        return id;
    }

    @Override
    public void updateDirector(Director director) {
        String sql = "UPDATE \"directors\" SET NAME = ? WHERE DIRECTOR_ID = ?";
        jdbcTemplate.update(sql,
                director.getName(),
                director.getId());
        log.info("Режиссер с id = {} обновлен", director.getId());
    }

    @Override
    public void deleteDirector(long directorId) {
        jdbcTemplate.update("DELETE FROM \"directors\" WHERE DIRECTOR_ID = ?", directorId);
        log.info("Удален режиссер с: id={}", directorId);
        log.info("Режиссер с идентификатором {} не найден.", directorId);
    }
}
