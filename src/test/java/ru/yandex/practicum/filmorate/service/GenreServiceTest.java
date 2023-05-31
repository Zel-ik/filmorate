package ru.yandex.practicum.filmorate.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import ru.yandex.practicum.filmorate.dao.GenreDao;
import ru.yandex.practicum.filmorate.entity.film.Genre;
import ru.yandex.practicum.filmorate.exception.GenreNotFoundException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class GenreServiceTest {

    @InjectMocks
    private GenreService genreService;

    @Mock
    private GenreDao genreDao;

    @Test
    public void getGenreById() {
        Optional<Genre> optionalGenre = Optional.of(Genre.builder()
                .id(1)
                .name("Комедия")
                .build());
        when(genreDao.getGenreById(Mockito.anyInt())).thenReturn(optionalGenre);
        Genre genre = genreService.getGenreById(1);
        assertNotNull(genre);
        assertEquals(1, genre.getId());
        assertEquals("Комедия", genre.getName());

        when(genreDao.getGenreById(Mockito.anyInt())).thenReturn(Optional.empty());
        assertThrows(GenreNotFoundException.class, () -> genreService.getGenreById(1));
    }

    @Test
    public void getAllGenres() {
        List<Genre> genreList = List.of(Genre.builder()
                        .id(1)
                        .build(),
                Genre.builder()
                        .id(2)
                        .build());
        when(genreDao.getAllGenres()).thenReturn(genreList);
        List<Genre> genreList1 = genreDao.getAllGenres();
        assertNotNull(genreList1);
        assertEquals(2, genreList1.size());
        assertEquals(1, genreList1.get(0).getId());
        assertEquals(2, genreList1.get(1).getId());
    }
}
