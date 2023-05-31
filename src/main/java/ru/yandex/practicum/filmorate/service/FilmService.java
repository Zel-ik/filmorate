package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.DirectorDao;
import ru.yandex.practicum.filmorate.dao.EventDao;
import ru.yandex.practicum.filmorate.dao.FilmDao;
import ru.yandex.practicum.filmorate.dao.UserDao;
import ru.yandex.practicum.filmorate.entity.Event;
import ru.yandex.practicum.filmorate.entity.EventType;
import ru.yandex.practicum.filmorate.entity.Operation;
import ru.yandex.practicum.filmorate.entity.film.Film;
import ru.yandex.practicum.filmorate.exception.DirectorNotFoundException;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.IncorrectParameterException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class FilmService {

    private final FilmDao filmDao;
    private final UserDao userDao;
    private final EventDao eventDao;
    private final DirectorDao directorDao;

    public Film getFilm(Long id) {
        return filmDao.getFilmById(id)
                .orElseThrow(() -> new FilmNotFoundException("Фильма с id = " + id + " не существует."));
    }

    public List<Film> getFilms() {
        return filmDao.getFilms();
    }

    public Film addFilm(Film film) {
        Long id = filmDao.addFilm(film);
        log.info("ПОЛУЧЕН ID = {}", id);
        return filmDao.getFilmById(id)
                .orElseThrow(() -> new FilmNotFoundException("Фильм не добавился"));
    }

    public Film updateFilm(Film film) {
        if (filmDao.getFilmById(film.getId()).isEmpty()) {
            throw new FilmNotFoundException("Фильма с id = " + film.getId() + " не существует.");
        }
        filmDao.updateFilm(film);
        return filmDao.getFilmById(film.getId()).get();
    }

    public Film addLike(Long filmId, Long userId) {
        if (filmDao.getFilmById(filmId).isEmpty()) {
            throw new FilmNotFoundException("Фильма с id = " + filmId + " не существует.");
        }
        if (userDao.getUserById(userId).isEmpty()) {
            throw new UserNotFoundException("Пользователя с id = " + userId + " не существует.");
        }
        eventDao.addEvent(Event.builder()
                .userId(userId)
                .eventType(EventType.LIKE)
                .operation(Operation.ADD)
                .timestamp(Instant.now().toEpochMilli())
                .entityId(filmId)
                .build());
        if (filmDao.isFilmLikeExist(filmId, userId)) {
            return filmDao.getFilmById(filmId).get();
        }
        filmDao.addLike(filmId, userId);
        log.trace("Создано событие: Пользователь: с id = " + userId + " поставил лайк фильму с id = " + filmId);
        return filmDao.getFilmById(filmId).get();
    }

    public Film deleteLike(Long filmId, Long userId) {
        if (filmDao.getFilmById(filmId).isEmpty()) {
            throw new FilmNotFoundException("Фильма с id = " + filmId + " не существует.");
        }
        if (userDao.getUserById(userId).isEmpty()) {
            throw new UserNotFoundException("Пользователя с id = " + userId + " не существует.");
        }
        filmDao.deleteLike(filmId, userId);
        eventDao.addEvent(Event.builder()
                .userId(userId)
                .eventType(EventType.LIKE)
                .operation(Operation.REMOVE)
                .timestamp(Instant.now().toEpochMilli())
                .entityId(filmId)
                .build());
        log.trace("Создано событие: Пользователь: с id = " + userId + " удалил лайк с фильма с id = " + filmId);
        return filmDao.getFilmById(filmId).get();
    }

    public List<Film> getPopularFilms(Integer genreId, Integer year, int count) {
        if (count < 1) {
            throw new IncorrectParameterException("count");
        } else if (year != null && year <= 1895) {
            throw new IncorrectParameterException("Год фильма не может быть равным или меньше 1895");
        }
        if (genreId == null & year == null) {
            return filmDao.getPopularFilms(count);
        }
        return filmDao.getPopularFilmsByGenreAndYear(genreId, year, count);
    }

    public List<Film> searchFilms(String query, String by) {
        if (query.isBlank() || by.isBlank()) {
            throw new IncorrectParameterException("query или by не должны быть пустыми");
        }
        query = query.toLowerCase();
        List<Film> rez;
        switch (by) {
            case "director":
                rez = filmDao.searchFilmsWithDirector(query);
                break;
            case "title":
                rez = filmDao.searchFilmsWithTitle(query);
                break;
            case "director,title":
            case "title,director":
                rez = filmDao.searchFilmsWithDirectorAndTitle(query);
                break;
            default:
                throw new IncorrectParameterException("by получил недопустимые параметры");
        }
        return rez;
    }

    public void deleteFilmById(Long filmId) {
        if (filmDao.getFilmById(filmId).isEmpty()) {
            throw new FilmNotFoundException(String.format("Фильма с id = '%s' не существует.", filmId));
        }
        filmDao.deleteFilmByFilmId(filmId);
    }

    public List<Film> getFilmsByDirectorId(Long directorId, SortBy sortBy) {
        if (directorDao.getDirectorById(directorId).isEmpty()) {
            throw new DirectorNotFoundException("Директора с id = " + directorId + " не существует.");
        }
        switch (sortBy) {
            case year:
                return filmDao.getFilmsByDirectorIdForYear(directorId);
            case likes:
                return filmDao.getFilmsByDirectorIdForLikes(directorId);
            default:
                throw new IncorrectParameterException("sortBy");
        }
    }

    public List<Film> getCommonFilms(Long userId, Long friendId) {
        if (userDao.getUserById(userId).isEmpty()) {
            throw new UserNotFoundException(String.format("Пользователя с id = '%s' не существует.", userId));
        }
        if (userDao.getUserById(userId).isEmpty()) {
            throw new UserNotFoundException(String.format("Пользователя с id = '%s' не существует.", friendId));
        }
        List<Film> filmsLikedByUser = filmDao.getFilmsLikedByUser(userId);
        List<Film> filmsLikedByFriend = filmDao.getFilmsLikedByUser(friendId);
        return filmsLikedByUser.stream()
                .filter(filmsLikedByFriend::contains)
                .collect(Collectors.toList());
    }
}
