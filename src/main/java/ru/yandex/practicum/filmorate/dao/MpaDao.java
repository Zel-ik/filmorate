package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.entity.film.Mpa;

import java.util.List;
import java.util.Optional;

public interface MpaDao {

    Optional<Mpa> getMpaById(int id);

    List<Mpa> getAllMpa();
}
