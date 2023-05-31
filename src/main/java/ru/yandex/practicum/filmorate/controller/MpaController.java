package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.entity.film.Mpa;
import ru.yandex.practicum.filmorate.service.MpaService;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
public class MpaController {

    private final MpaService mpaService;

    @GetMapping(value = "/mpa/{id}")
    public Mpa getMpaById(@PathVariable int id) {
        Mpa mpa = mpaService.getMpaById(id);
        log.trace("MPA рейтинг получен: {}", mpa);
        return mpa;
    }

    @GetMapping(value = "/mpa")
    public List<Mpa> getMpa() {
        List<Mpa> mpaList = mpaService.getAllMpa();
        log.trace("Список MPA рейтингов получен, кол-во = {}", mpaList.size());
        return mpaList;
    }
}
