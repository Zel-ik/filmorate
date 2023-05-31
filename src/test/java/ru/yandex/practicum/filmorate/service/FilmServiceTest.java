package ru.yandex.practicum.filmorate.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import ru.yandex.practicum.filmorate.dao.FilmDao;
import ru.yandex.practicum.filmorate.dao.UserDao;
import ru.yandex.practicum.filmorate.entity.film.Film;
import ru.yandex.practicum.filmorate.entity.film.Mpa;
import ru.yandex.practicum.filmorate.entity.user.User;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.IncorrectParameterException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class FilmServiceTest {

    @InjectMocks
    private FilmService filmService;

    @Mock
    private UserDao userDao;

    @Mock
    private FilmDao filmDao;

    @Test
    public void getFilm() {
        Optional<Film> optionalFilm = Optional.of(Film.builder()
                .id(1L)
                .name("name1")
                .description("description1")
                .releaseDate(LocalDate.now())
                .duration(100)
                .rate(2)
                .mpa(Mpa.builder().id(1).build())
                .build());
        when(filmDao.getFilmById(Mockito.anyLong())).thenReturn(optionalFilm);
        Film film = filmService.getFilm(1L);
        assertNotNull(film);
        assertEquals(1L, film.getId());

        when(filmDao.getFilmById(Mockito.anyLong())).thenReturn(Optional.empty());
        assertThrows(FilmNotFoundException.class, () -> filmService.getFilm(1L));
    }

    @Test
    public void addFilm() {
        Film film = Film.builder()
                .name("name1")
                .description("description1")
                .releaseDate(LocalDate.now())
                .duration(100)
                .rate(2)
                .mpa(Mpa.builder().id(1).build())
                .build();
        Optional<Film> optionalFilm = Optional.of(Film.builder()
                .id(1L)
                .name("name1")
                .description("description1")
                .releaseDate(LocalDate.now())
                .duration(100)
                .rate(2)
                .mpa(Mpa.builder().id(1).build())
                .build());
        when(filmDao.getFilmById(Mockito.any(Long.class))).thenReturn(optionalFilm);
        Film addedFilm = filmService.addFilm(film);
        assertNotNull(addedFilm);
        assertEquals(optionalFilm.get(), addedFilm);
        assertEquals(1, addedFilm.getId());

        when(filmDao.getFilmById(Mockito.anyLong())).thenReturn(Optional.empty());
        assertThrows(FilmNotFoundException.class, () -> filmService.addFilm(film));
    }

    @Test
    public void updateFilm() {
        Film film = Film.builder()
                .id(1L)
                .name("name1")
                .description("description1")
                .releaseDate(LocalDate.now())
                .duration(100)
                .rate(2)
                .mpa(Mpa.builder().id(1).build())
                .build();
        Optional<Film> optionalFilm = Optional.of(Film.builder()
                .id(1L)
                .name("newName")
                .description("newDescription")
                .releaseDate(LocalDate.now())
                .duration(125)
                .rate(4)
                .mpa(Mpa.builder().id(2).build())
                .build());
        when(filmDao.getFilmById(Mockito.any(Long.class))).thenReturn(optionalFilm);
        Film updatedFilm = filmService.updateFilm(film);
        assertNotNull(updatedFilm);
        assertEquals(1, updatedFilm.getId());
        assertEquals("newName", updatedFilm.getName());
        assertEquals("newDescription", updatedFilm.getDescription());
        assertEquals(125, updatedFilm.getDuration());
        assertEquals(4, updatedFilm.getRate());
        assertEquals(2, updatedFilm.getMpa().getId());

        when(filmDao.getFilmById(Mockito.anyLong())).thenReturn(Optional.empty());
        assertThrows(FilmNotFoundException.class, () -> filmService.updateFilm(film));
    }

    @Test
    public void getFilms() {
        List<Film> returnedFilms = List.of(Film.builder()
                        .id(1L)
                        .build(),
                Film.builder()
                        .id(2L)
                        .build());
        when(filmDao.getFilms()).thenReturn(returnedFilms);
        List<Film> films = filmService.getFilms();
        assertNotNull(films);
        assertEquals(returnedFilms, films);
    }

    @Test
    public void getMostLikesFilms() {
        Film film1 = Film.builder()
                .id(1L)
                .build();
        Film film2 = Film.builder()
                .id(2L)
                .build();
        List<Film> popularFilms = List.of(film1, film2);
        when(filmDao.getPopularFilms(Mockito.any(Integer.class))).thenReturn(popularFilms);
        List<Film> films = filmService.getPopularFilms(null, null, 2);
        assertNotNull(films);
        assertEquals(2, films.size());
        assertTrue(films.contains(film1));
        assertTrue(films.contains(film2));

        assertThrows(IncorrectParameterException.class, () -> filmService.getPopularFilms(null, null, 0));
    }

    @Test
    public void addLike() {
        doNothing().when(filmDao).addLike(Mockito.anyLong(), Mockito.anyLong());
        Optional<Film> optionalFilm = Optional.of(Film.builder().id(1L).build());
        when(userDao.getUserById(Mockito.anyLong())).thenReturn(Optional.empty());
        when(filmDao.getFilmById(Mockito.anyLong())).thenReturn(optionalFilm);
        filmDao.addLike(1L, 1L);
        assertThrows(UserNotFoundException.class, () -> filmService.addLike(1L, 1L));
        when(filmDao.getFilmById(Mockito.anyLong())).thenReturn(Optional.empty());
        assertThrows(FilmNotFoundException.class, () -> filmService.addLike(1L, 1L));
    }

    @Test
    public void deleteLike() {
        doNothing().when(filmDao).deleteLike(Mockito.anyLong(), Mockito.anyLong());
        Optional<Film> optionalFilm = Optional.of(Film.builder().id(1L).build());
        when(userDao.getUserById(Mockito.anyLong())).thenReturn(Optional.empty());
        when(filmDao.getFilmById(Mockito.anyLong())).thenReturn(optionalFilm);
        filmDao.deleteLike(1L, 1L);
        assertThrows(UserNotFoundException.class, () -> filmService.deleteLike(1L, 1L));
        when(filmDao.getFilmById(Mockito.anyLong())).thenReturn(Optional.empty());
        assertThrows(FilmNotFoundException.class, () -> filmService.deleteLike(1L, 1L));
    }

    @Test
    public void getCommonFriends() {
        List<Film> likedFilmsByUser = List.of(Film.builder().id(1L).build(), Film.builder().id(2L).build());
        List<Film> likedFilmsByFriend = List.of(Film.builder().id(2L).build());
        when(userDao.getUserById(Mockito.anyLong())).thenReturn(Optional.of(User.builder().build()));
        when(filmDao.getFilmsLikedByUser(1L)).thenReturn(likedFilmsByUser);
        when(filmDao.getFilmsLikedByUser(2L)).thenReturn(likedFilmsByFriend);
        List<Film> commonFilms = filmService.getCommonFilms(1L, 2L);
        assertNotNull(commonFilms);
        assertEquals(1, commonFilms.size());
        assertEquals(2, commonFilms.get(0).getId());
        when(userDao.getUserById(Mockito.anyLong())).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> filmService.getCommonFilms(1L, 2L));
    }
}
