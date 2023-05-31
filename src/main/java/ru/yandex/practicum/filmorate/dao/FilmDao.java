package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.entity.film.Film;
import ru.yandex.practicum.filmorate.entity.film.FilmLikes;

import java.util.List;
import java.util.Optional;

public interface FilmDao {

    Optional<Film> getFilmById(long id);

    List<Film> getFilms();

    Long addFilm(Film film);

    void updateFilm(Film film);

    List<Film> getPopularFilms(int count);

    List<Film> getPopularFilmsByGenreAndYear(Integer genreId, Integer year, int count);

    List<Film> getFilmsByDirectorIdForYear(long directorId);

    List<Film> getFilmsByDirectorIdForLikes(long directorId);

    void addLike(long filmId, long userId);

    void deleteLike(long filmId, long userId);

    List<FilmLikes> findAllFilmLikes();

    void deleteFilmByFilmId(long filmId);

    List<Film> searchFilmsWithDirectorAndTitle(String query);

    List<Film> searchFilmsWithDirector(String query);

    List<Film> searchFilmsWithTitle(String query);

    List<Film> getFilmsLikedByUser(long userId);

    boolean isFilmLikeExist(long filmId, long userId);
}
