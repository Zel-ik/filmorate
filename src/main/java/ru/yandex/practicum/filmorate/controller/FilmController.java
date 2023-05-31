package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.entity.film.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.SortBy;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
public class FilmController {

    private final FilmService filmService;

    private final UserService userService;

    @GetMapping(value = "/films")
    public List<Film> findAll() {
        List<Film> films = filmService.getFilms();
        log.trace("Кол-во фильмов: " + films.size());
        return films;
    }

    @GetMapping(value = "/films/{id}")
    public Film getFilm(@PathVariable Long id) {
        Film film = filmService.getFilm(id);
        log.trace("Фильм получен: " + film);
        return film;
    }

    @GetMapping(value = "/films/popular")
    public List<Film> getPopularFilms(
            @RequestParam(value = "count", defaultValue = "10", required = false) int count,
            @RequestParam(value = "genreId", required = false) Integer genreId,
            @RequestParam(value = "year", required = false) Integer year) {
        log.trace("Топ " + count + " фильмов по лайкам получен.");
        return filmService.getPopularFilms(genreId, year, count);
    }

    @GetMapping(value = "films/director/{directorId}")
    public List<Film> getFilmsByDirectorId(@PathVariable Long directorId,
                                           @RequestParam(value = "sortBy") SortBy sortBy) {
        log.trace("Список фильмов с сортировкой по " + sortBy + " получен.");
        return filmService.getFilmsByDirectorId(directorId, sortBy);
    }

    @GetMapping("/films/search")
    public List<Film> searchFilms(@RequestParam String query, @RequestParam String by) {
        log.trace("обрабатываем поисковой запрос. Query =" + query + " , параметр =" + by);
        return filmService.searchFilms(query, by);
    }

    @PostMapping(value = "/films")
    public Film addFilm(@Valid @RequestBody Film film) {
        Film addedfilm = filmService.addFilm(film);
        log.trace("Фильм добавлен: " + addedfilm);
        return addedfilm;
    }

    @PutMapping(value = "/films")
    public Film updateFilm(@Valid @RequestBody Film film) {
        Film updadedFilm = filmService.updateFilm(film);
        log.trace("Фильм обновлен: " + updadedFilm);
        return updadedFilm;
    }

    @PutMapping(value = "/films/{id}/like/{userId}")
    public Film addLike(
            @PathVariable Long id,
            @PathVariable Long userId) {
        log.trace("Пользователь: " + userService.getUser(userId) + " поставил лайк фильму: " + filmService.getFilm(id));
        return filmService.addLike(id, userId);
    }

    @DeleteMapping(value = "/films/{id}/like/{userId}")
    public Film deleteLike(
            @PathVariable Long id,
            @PathVariable Long userId) {
        log.trace("Пользователь: " + userService.getUser(userId) + " удалил лайк фильму: " + filmService.getFilm(id));
        return filmService.deleteLike(id, userId);
    }

    @DeleteMapping(value = "/films/{filmId}")
    public void deleteFilmById(@PathVariable Long filmId) {
        filmService.deleteFilmById(filmId);
        log.trace("Фильм id={} удален", filmId);
    }

    @GetMapping(value = "/films/common")
    public List<Film> getCommonFilms(@RequestParam(name = "userId") Long userId,
                                     @RequestParam(name = "friendId") Long friendId) {
        List<Film> commonFilms = filmService.getCommonFilms(userId, friendId);
        log.trace("Список общих фильмов получен, длина={}", commonFilms);
        return commonFilms;
    }
}
