package ru.yandex.practicum.filmorate.dao.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.DirectorDao;
import ru.yandex.practicum.filmorate.dao.FilmDao;
import ru.yandex.practicum.filmorate.dao.GenreDao;
import ru.yandex.practicum.filmorate.dao.MpaDao;
import ru.yandex.practicum.filmorate.entity.film.Director;
import ru.yandex.practicum.filmorate.entity.film.Film;
import ru.yandex.practicum.filmorate.entity.film.FilmLikes;
import ru.yandex.practicum.filmorate.entity.film.Genre;
import ru.yandex.practicum.filmorate.exception.MpaNotFoundException;

import java.sql.Date;
import java.sql.*;
import java.util.*;

@Component
@Slf4j
@RequiredArgsConstructor
public class FilmDaoImpl implements FilmDao {

    private final JdbcTemplate jdbcTemplate;
    private final MpaDao mpaDao;
    private final GenreDao genreDao;
    private final DirectorDao directorDao;

    @Override
    public Optional<Film> getFilmById(long id) {
        SqlRowSet filmRow = jdbcTemplate.queryForRowSet("SELECT * FROM \"films\" f WHERE FILM_ID = ?;", id);
        if (filmRow.next()) {
            Film film = Film.builder()
                    .id(filmRow.getLong("FILM_ID"))
                    .name(Objects.requireNonNull(filmRow.getString("NAME")).strip())
                    .description(Objects.requireNonNull(filmRow.getString("DESCRIPTION")).strip())
                    .releaseDate(Objects.requireNonNull(filmRow.getDate("RELEASE_DATE")).toLocalDate())
                    .duration(filmRow.getInt("DURATION"))
                    .rate(filmRow.getInt("RATE"))
                    .mpa(mpaDao.getMpaById(filmRow.getInt("MPA_ID"))
                            .orElseThrow(() -> new MpaNotFoundException(" ")))
                    .directors(getFilmDirectorsById(filmRow.getLong("FILM_ID")))
                    .genres(getFilmGenresById(filmRow.getLong("FILM_ID")))
                    .build();
            log.info("Найден фильм c id = {} ", film.getId());
            return Optional.of(film);
        } else {
            log.info("Фильм с id = {} не найден.", id);
            return Optional.empty();
        }
    }

    @Override
    public List<Film> getFilms() {
        String sql = "SELECT * FROM \"films\"";
        List<Film> films = jdbcTemplate.query(sql, this::mapRowToFilm);
        log.info("Список фильмов получен. Длина = {}", films.size());
        return films;
    }

    @Override
    public Long addFilm(Film film) {
        String sqlFilms = "INSERT INTO \"films\" (NAME, DESCRIPTION, RELEASE_DATE, DURATION, RATE, MPA_ID) VALUES" +
                " (?,?,?,?,?,?);";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(conn -> {
            PreparedStatement preparedStatement = conn.prepareStatement(sqlFilms, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, film.getName().strip());
            preparedStatement.setString(2, film.getDescription().strip());
            preparedStatement.setDate(3, Date.valueOf(film.getReleaseDate()));
            preparedStatement.setInt(4, film.getDuration());
            preparedStatement.setInt(5, 0);
            if (film.getMpa() == null) {
                preparedStatement.setNull(6, java.sql.Types.NULL);
            } else {
                preparedStatement.setInt(6, film.getMpa().getId());
            }
            return preparedStatement;
        }, keyHolder);
        Long id = Objects.requireNonNull(keyHolder.getKey()).longValue();
        if (film.getGenres() != null) {
            film.getGenres().forEach(genre -> addFilmsGenre(id, genre.getId()));
        }
        if (film.getDirectors() != null) {
            film.getDirectors().forEach(director -> addFilmsDirector(id, director.getId()));
        }
        log.info("Фильм с id={} добавлен", id);
        return id;
    }

    @Override
    public void updateFilm(Film film) {
        String sql = "UPDATE \"films\" SET NAME = ?, DESCRIPTION = ?, RELEASE_DATE = ?, DURATION = ?" +
                ", MPA_ID = ? WHERE FILM_ID = ?";
        if (film.getRate() == null) {
            film.setRate(0);
        }
        jdbcTemplate.update(sql,
                film.getName(),
                film.getDescription(),
                Date.valueOf(film.getReleaseDate()),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId());
        deleteFilmGenres(film.getId());
        if (film.getGenres() != null) {
            film.getGenres().forEach(genre -> addFilmsGenre(film.getId(), genre.getId()));
        }
        deleteFilmDirectors(film.getId());
        if (film.getDirectors() != null) {
            film.getDirectors().forEach(director -> addFilmsDirector(film.getId(), director.getId()));
        }
        log.info("Фильм с id = {} обновлен", film.getId());
    }

    @Override
    public List<Film> getPopularFilms(int count) {
        String sql = "SELECT * FROM \"films\" f ORDER BY RATE DESC LIMIT ?;";
        List<Film> films = jdbcTemplate.query(sql, this::mapRowToFilm, count);
        log.info("Список популярных фильмов получен. Длина = {}", films.size());
        return films;
    }

    @Override
    public List<Film> getPopularFilmsByGenreAndYear(Integer genreId, Integer year, int count) {
        String sql = "SELECT " +
                "DISTINCT f.film_id, " +
                "f.name, " +
                "f.description, " +
                "f.release_date, " +
                "f.duration, " +
                "f.rate, " +
                "f.mpa_id " +
                "FROM \"films\" f JOIN \"films_genres\" fg ON f.FILM_ID = fg.FILM_ID  " +
                "WHERE fg.GENRE_ID = (SELECT COALESCE(?, fg.GENRE_ID))" +
                "AND EXTRACT(YEAR FROM CAST(f.RELEASE_DATE AS date)) " +
                "= (SELECT COALESCE(?, EXTRACT(YEAR FROM CAST(f.RELEASE_DATE AS date)))) " +
                "ORDER BY RATE DESC LIMIT ?;";
        List<Film> films = jdbcTemplate.query(sql, this::mapRowToFilm, genreId, year, count);
        log.info("Список популярных фильмов получен. Длина = {}", films.size());
        return films;
    }

    @Override
    public List<Film> searchFilmsWithDirectorAndTitle(String query) {
        query = "%" + query + "%";
        String sqlQuery = "SELECT  DISTINCT f.FILM_ID , f.NAME , f.DESCRIPTION , f.RELEASE_DATE , f.DURATION , f.RATE , f.MPA_ID " +
                "FROM \"films\" f " +
                "left JOIN \"films_directors\" t ON f.FILM_ID =t.FILM_ID " +
                "left JOIN \"directors\" d ON t.DIRECTOR_ID  = d.DIRECTOR_ID " +
                " where lower(d.NAME) like ? or lower(f.NAME) like ? Order by f.FILM_ID DESC";
        List<Film> films = jdbcTemplate.query(sqlQuery, this::mapRowToFilm, query, query);
        log.info("Список запрашиваемых фильмов получен. Длина = {}", films.size());
        return films;
    }

    @Override
    public List<Film> searchFilmsWithDirector(String query) {
        query = "%" + query + "%";
        String sqlQuery = "SELECT  DISTINCT f.FILM_ID , f.NAME , f.DESCRIPTION , f.RELEASE_DATE , f.DURATION , f.RATE , f.MPA_ID " +
                "FROM \"films\" f " +
                "left JOIN \"films_directors\" t ON f.FILM_ID =t.FILM_ID " +
                "left JOIN \"directors\" d ON t.DIRECTOR_ID  = d.DIRECTOR_ID " +
                " where lower(d.NAME) like ? Order by f.FILM_ID DESC";
        List<Film> films = jdbcTemplate.query(sqlQuery, this::mapRowToFilm, query);
        log.info("Список запрашиваемых фильмов получен. Длина = {}", films.size());
        return films;
    }

    @Override
    public List<Film> searchFilmsWithTitle(String query) {
        query = "%" + query + "%";
        String sqlQuery = "SELECT  DISTINCT f.FILM_ID , f.NAME , f.DESCRIPTION , f.RELEASE_DATE , f.DURATION , f.RATE , f.MPA_ID " +
                "FROM \"films\" f " +
                "left JOIN \"films_directors\" t ON f.FILM_ID =t.FILM_ID " +
                "left JOIN \"directors\" d ON t.DIRECTOR_ID  = d.DIRECTOR_ID " +
                " where lower(f.NAME) like ? Order by f.FILM_ID DESC  ";
        List<Film> films = jdbcTemplate.query(sqlQuery, this::mapRowToFilm, query);
        log.info("Список запрашиваемых фильмов получен. Длина = {}", films.size());
        return films;
    }

    @Override
    public List<Film> getFilmsByDirectorIdForYear(long directorId) {
        String sql = "SELECT * FROM \"films\" f JOIN \"films_directors\" fd ON f.FILM_ID = fd.FILM_ID " +
                "WHERE fd.DIRECTOR_ID = ? " +
                "ORDER BY f.RELEASE_DATE";
        List<Film> films = jdbcTemplate.query(sql, this::mapRowToFilm, directorId);
        log.info("Список фильмов получен. Длина = {}", films.size());
        return films;
    }

    @Override
    public List<Film> getFilmsByDirectorIdForLikes(long directorId) {
        String sql = "SELECT * " +
                "FROM \"films\" f " +
                "JOIN \"films_directors\" fd ON f.film_id = fd.film_id " +
                "WHERE fd.director_id = ? " +
                "ORDER BY f.rate DESC";
        List<Film> films = jdbcTemplate.query(sql, this::mapRowToFilm, directorId);
        log.info("Список фильмов получен. Длина = {}", films.size());
        return films;
    }

    @Override
    public void addLike(long filmId, long userId) {
        String sqlFilmsLikes = "INSERT INTO \"film_likes\" (FILM_ID, USER_ID) VALUES (?,?)";
        jdbcTemplate.update(sqlFilmsLikes, filmId, userId);
        String sqlFilms = "UPDATE \"films\" SET RATE = ((SELECT RATE FROM \"films\" f" +
                " WHERE FILM_ID = ?) + 1) WHERE FILM_ID = ?;";
        jdbcTemplate.update(sqlFilms, filmId, filmId);
        log.info("Фильму id={} поставил лайк пользователь id={}", filmId, userId);
    }

    @Override
    public void deleteLike(long filmId, long userId) {
        String sqlFilmsLikes = "DELETE FROM \"film_likes\" WHERE FILM_ID = ? AND USER_ID = ?";
        jdbcTemplate.update(sqlFilmsLikes, filmId, userId);
        String sqlFilms = "UPDATE \"films\" SET RATE = ((SELECT RATE FROM \"films\" f" +
                " WHERE FILM_ID = ?) - 1) WHERE FILM_ID = ?;";
        jdbcTemplate.update(sqlFilms, filmId, filmId);
        log.info("У фильма id={} удалил лайк пользователь id={}", filmId, userId);
    }

    private Film mapRowToFilm(ResultSet resultSet, int rowNum) throws SQLException {
        return Film.builder()
                .id(resultSet.getLong("FILM_ID"))
                .name(resultSet.getString("NAME"))
                .description(resultSet.getString("DESCRIPTION"))
                .releaseDate(Objects.requireNonNull(resultSet.getDate("RELEASE_DATE")).toLocalDate())
                .duration(resultSet.getInt("DURATION"))
                .rate(resultSet.getInt("RATE"))
                .mpa(mpaDao.getMpaById(resultSet.getInt("MPA_ID"))
                        .orElseThrow(() -> new MpaNotFoundException("МПА рейтинг не найден")))
                .genres(getFilmGenresById(resultSet.getLong("FILM_ID")))
                .directors(getFilmDirectorsById(resultSet.getLong("FILM_ID")))
                .build();
    }

    private Set<Genre> getFilmGenresById(Long filmId) {
        String sql = "SELECT fg.GENRE_ID, g.NAME FROM \"films_genres\" fg INNER JOIN \"genres\" g" +
                " ON g.GENRE_ID = fg.GENRE_ID WHERE FILM_ID = ?;";
        Set<Genre> genres = new HashSet<>(jdbcTemplate.query(sql, (rs, rowNum) -> Genre.builder()
                .id(rs.getInt("GENRE_ID"))
                .name(rs.getString("NAME").strip())
                .build(), filmId));
        log.info("Получен список жанров фильма id={}, длина списка = {}", filmId, genres.size());
        return genres;
    }

    private Set<Director> getFilmDirectorsById(Long filmId) {
        String sql = "SELECT fd.DIRECTOR_ID, d.NAME FROM \"films_directors\" fd INNER JOIN \"directors\" d" +
                " ON d.DIRECTOR_ID = fd.DIRECTOR_ID WHERE FILM_ID = ?;";
        Set<Director> directors = new HashSet<>(jdbcTemplate.query(sql, (rs, rowNum) -> Director.builder()
                .id(rs.getLong("DIRECTOR_ID"))
                .name(rs.getString("NAME").strip())
                .build(), filmId));
        log.info("Получен список режиссеров фильма id={}, длина списка = {}", filmId, directors.size());
        return directors;
    }

    private void addFilmsGenre(Long filmId, int genreId) {
        if (this.getFilmById(filmId).isEmpty()) {
            log.info("Фильма с id = {} не существует", filmId);
            return;
        }
        if (genreDao.getGenreById(genreId).isEmpty()) {
            log.info("Жанра с id = {} не существует", genreId);
            return;
        }
        String sql = "INSERT INTO \"films_genres\" (FILM_ID, GENRE_ID) VALUES (?, ?);";
        jdbcTemplate.update(sql, filmId, genreId);
        log.info("Фильму id={} добавился жанр id={}", filmId, genreId);
    }

    private void addFilmsDirector(Long filmId, Long directorId) {
        if (this.getFilmById(filmId).isEmpty()) {
            log.info("Фильма с id = {} не существует", filmId);
            return;
        }
        if (directorDao.getDirectorById(directorId).isEmpty()) {
            log.info("Режиссер с id = {} не существует", directorId);
            return;
        }
        String sql = "INSERT INTO \"films_directors\" (FILM_ID, DIRECTOR_ID) VALUES (?, ?);";
        jdbcTemplate.update(sql, filmId, directorId);
        log.info("Фильму id={} добавился режиссер id={}", filmId, directorId);
    }

    private void deleteFilmGenres(Long filmId) {
        if (this.getFilmById(filmId).isEmpty()) {
            log.info("Фильма с id = {} не существует", filmId);
            return;
        }
        String sql = "DELETE FROM \"films_genres\" WHERE FILM_ID = ?;";
        jdbcTemplate.update(sql, filmId);
        log.info("У фильма id={} удалились все жанры", filmId);
    }

    private void deleteFilmDirectors(Long filmId) {
        if (this.getFilmById(filmId).isEmpty()) {
            log.info("Фильма с id = {} не существует", filmId);
            return;
        }
        String sql = "DELETE FROM \"films_directors\" WHERE FILM_ID = ?;";
        jdbcTemplate.update(sql, filmId);
        log.info("У фильма id={} удалились все режиссеры", filmId);
    }

    @Override
    public List<FilmLikes> findAllFilmLikes() {
        String sql = "SELECT * FROM \"film_likes\"";
        List<FilmLikes> filmLikes = jdbcTemplate.query(sql, (rs, rowNum) -> FilmLikes.builder()
                .filmId(rs.getLong("FILM_ID"))
                .userId(rs.getLong("USER_ID"))
                .build());
        log.info("Список фильмов с лайками получен. Длина = {}", filmLikes.size());
        return filmLikes;
    }

    @Override
    public void deleteFilmByFilmId(long filmId) {
        String sql = "DELETE FROM \"films\" WHERE FILM_ID = ?;";
        jdbcTemplate.update(sql, filmId);
    }

    @Override
    public List<Film> getFilmsLikedByUser(long userId) {
        String sql = "SELECT * FROM \"films\" f \n" +
                "INNER JOIN \"film_likes\" fl ON f.FILM_ID = fl.FILM_ID \n" +
                "WHERE fl.USER_ID = ?\n" +
                "ORDER BY f.RATE DESC;";
        List<Film> likedFilms = jdbcTemplate.query(sql, this::mapRowToFilm, userId);
        log.info("Список понравившихся фильмов получен. Длина = {}", likedFilms.size());
        return likedFilms;
    }

    @Override
    public boolean isFilmLikeExist(long filmId, long userId) {
        String sql = "SELECT * FROM \"film_likes\" fl WHERE FILM_ID = ? AND USER_ID = ?;";
        SqlRowSet row = jdbcTemplate.queryForRowSet(sql, filmId, userId);
        return row.next();
    }
}
