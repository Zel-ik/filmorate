package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.entity.film.Director;

import java.util.List;
import java.util.Optional;

public interface DirectorDao {

    Optional<Director> getDirectorById(long id);

    List<Director> getAllDirector();

    Long addDirector(Director director);

    void updateDirector(Director director);

    void deleteDirector(long directorId);
}
