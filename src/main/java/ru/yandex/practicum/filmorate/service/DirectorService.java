package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.DirectorDao;
import ru.yandex.practicum.filmorate.entity.film.Director;
import ru.yandex.practicum.filmorate.exception.DirectorNotFoundException;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class DirectorService {
    private final DirectorDao directorDao;

    public Director getDirector(Long id) {
        return directorDao.getDirectorById(id)
                .orElseThrow(() -> new DirectorNotFoundException("Режиссера с id = " + id + " не существует."));
    }

    public List<Director> getDirectors() {
        return directorDao.getAllDirector();
    }

    public Director addDirector(Director director) {
        Long id = directorDao.addDirector(director);
        log.info("ПОЛУЧЕН ID = {}", id);
        return directorDao.getDirectorById(id)
                .orElseThrow(() -> new DirectorNotFoundException("Режиссер не добавился"));
    }

    public Director updateDirector(Director director) {
        if (directorDao.getDirectorById(director.getId()).isEmpty()) {
            throw new DirectorNotFoundException("Режиссер с id = " + director.getId() + " не существует.");
        }
        directorDao.updateDirector(director);
        return directorDao.getDirectorById(director.getId()).get();
    }

    public void deleteDirector(Long id) {
        if (directorDao.getDirectorById(id).isEmpty()) {
            throw new DirectorNotFoundException("Режиссер с id = " + id + " не существует.");
        }
        directorDao.deleteDirector(id);
    }
}
