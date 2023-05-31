package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.entity.film.Director;
import ru.yandex.practicum.filmorate.service.DirectorService;

import javax.validation.Valid;
import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
public class DirectorController {

    private final DirectorService directorService;

    @GetMapping(value = "/directors")
    public List<Director> findAll() {
        List<Director> directors = directorService.getDirectors();
        log.trace("Кол-во режиссеров: " + directors.size());
        return directors;
    }

    @GetMapping(value = "/directors/{id}")
    public Director getDirector(@PathVariable Long id) {
        Director director = directorService.getDirector(id);
        log.trace("Режиссер получен: " + director);
        return director;
    }

    @PostMapping(value = "/directors")
    public Director addDirector(@Valid @RequestBody Director director) {
        Director addedDirector = directorService.addDirector(director);
        log.trace("Режиссер добавлен: " + addedDirector);
        return addedDirector;
    }

    @PutMapping(value = "/directors")
    public Director updateDirector(@Valid @RequestBody Director director) {
        Director updadedDirector = directorService.updateDirector(director);
        log.trace("Режиссер обновлен: " + updadedDirector);
        return updadedDirector;
    }

    @DeleteMapping(value = "/directors/{directorId}")
    public void deleteDirector(@PathVariable Long directorId) {
        log.trace("Режиссер: " + directorService.getDirector(directorId) + " удален.");
        directorService.deleteDirector(directorId);
    }
}
