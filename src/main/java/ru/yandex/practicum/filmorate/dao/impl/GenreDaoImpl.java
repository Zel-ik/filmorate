package ru.yandex.practicum.filmorate.dao.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.GenreDao;
import ru.yandex.practicum.filmorate.entity.film.Genre;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Component
@Slf4j
@RequiredArgsConstructor
public class GenreDaoImpl implements GenreDao {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Optional<Genre> getGenreById(int id) {
        SqlRowSet genreRows = jdbcTemplate.queryForRowSet("SELECT * FROM \"genres\" g WHERE GENRE_ID = ?", id);
        if (genreRows.next()) {
            Genre genre = Genre.builder()
                    .id(genreRows.getInt("GENRE_ID"))
                    .name(Objects.requireNonNull(genreRows.getString("NAME")).strip())
                    .build();
            log.info("Найден жанр: {}", genre);
            return Optional.of(genre);
        } else {
            log.info("Жанр с идентификатором {} не найден.", id);
            return Optional.empty();
        }
    }

    @Override
    public List<Genre> getAllGenres() {
        String sql = "SELECT * FROM \"genres\" g;";
        List<Genre> genreList = jdbcTemplate.query(sql, (rs, rowNum) -> Genre.builder()
                .id(rs.getInt("GENRE_ID"))
                .name(rs.getString("NAME").strip())
                .build());
        log.info("Список жанров получен, длина = {}", genreList.size());
        return genreList;
    }
}
