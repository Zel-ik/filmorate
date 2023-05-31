package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.MpaDao;
import ru.yandex.practicum.filmorate.entity.film.Mpa;
import ru.yandex.practicum.filmorate.exception.MpaNotFoundException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MpaService {

    private final MpaDao mpaDao;

    public Mpa getMpaById(int id) {
        return mpaDao.getMpaById(id)
                .orElseThrow(() -> new MpaNotFoundException("Рейтинга с id = " + id + " не существует."));
    }

    public List<Mpa> getAllMpa() {
        return mpaDao.getAllMpa();
    }
}
