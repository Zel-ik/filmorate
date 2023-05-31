package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.entity.film.Genre;

import java.util.List;
import java.util.Optional;

public interface GenreDao {

    Optional<Genre> getGenreById(int id);

    List<Genre> getAllGenres();
}
