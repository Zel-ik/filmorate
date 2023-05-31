package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.entity.film.Genre;
import ru.yandex.practicum.filmorate.service.GenreService;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
public class GenresController {

    private final GenreService genreService;

    @GetMapping(value = "/genres/{id}")
    public Genre getGenreById(@PathVariable int id) {
        Genre genre = genreService.getGenreById(id);
        log.trace("Жанр получен: {}", genre);
        return genre;
    }

    @GetMapping(value = "/genres")
    public List<Genre> getGenres() {
        List<Genre> genreList = genreService.getAllGenres();
        log.trace("Список жанров фильмов получен, кол-во = {}", genreList.size());
        return genreList;
    }
}
