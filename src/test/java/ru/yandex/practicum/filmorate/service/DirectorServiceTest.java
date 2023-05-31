package ru.yandex.practicum.filmorate.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import ru.yandex.practicum.filmorate.dao.DirectorDao;
import ru.yandex.practicum.filmorate.entity.film.Director;
import ru.yandex.practicum.filmorate.exception.DirectorNotFoundException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DirectorServiceTest {

    @InjectMocks
    private DirectorService directorService;

    @Mock
    private DirectorDao directorDao;

    @Test
    public void getDirectorById() {
        Optional<Director> optionalDirector = Optional.of(Director.builder().id(1L).name("name").build());
        when(directorDao.getDirectorById(Mockito.anyLong())).thenReturn(optionalDirector);
        Director director = directorService.getDirector(1L);
        assertNotNull(director);
        assertEquals(1L, director.getId());
        when(directorDao.getDirectorById(Mockito.anyLong())).thenReturn(Optional.empty());
        assertThrows(DirectorNotFoundException.class, () -> directorService.getDirector(1L));
    }

    @Test
    public void getAllDirectors() {
        List<Director> directors = List.of(Director.builder().id(1L).name("name").build());
        when(directorDao.getAllDirector()).thenReturn(directors);
        List<Director> directorsList = directorService.getDirectors();
        assertNotNull(directorsList);
        assertEquals(1L, directorsList.get(0).getId());
    }

    @Test
    public void addDirector() {
        Optional<Director> optionalDirector = Optional.of(Director.builder().id(1L).name("name").build());
        when(directorDao.getDirectorById(Mockito.anyLong())).thenReturn(optionalDirector);
        Director director = directorService.addDirector(optionalDirector.get());
        assertNotNull(director);
        assertEquals(1L, director.getId());
    }

    @Test
    public void updateDirectorById() {
        Optional<Director> optionalDirector = Optional.of(Director.builder().id(1L).name("name").build());
        when(directorDao.getDirectorById(Mockito.anyLong())).thenReturn(optionalDirector);
        Director director = directorService.updateDirector(optionalDirector.get());
        assertNotNull(director);
        assertEquals(1L, director.getId());
        when(directorDao.getDirectorById(Mockito.anyLong())).thenReturn(Optional.empty());
        assertThrows(DirectorNotFoundException.class, () -> directorService.getDirector(1L));
    }
}
