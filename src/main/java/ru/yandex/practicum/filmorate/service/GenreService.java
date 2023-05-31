package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.GenreDao;
import ru.yandex.practicum.filmorate.entity.film.Genre;
import ru.yandex.practicum.filmorate.exception.GenreNotFoundException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GenreService {

    private final GenreDao genreDao;

    public Genre getGenreById(int id) {
        return genreDao.getGenreById(id)
                .orElseThrow(() -> new GenreNotFoundException("Жанра с id = " + id + " не существует."));
    }

    public List<Genre> getAllGenres() {
        return genreDao.getAllGenres();
    }
}
