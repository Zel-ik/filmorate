package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.entity.film.Film;
import ru.yandex.practicum.filmorate.entity.film.FilmLikes;
import ru.yandex.practicum.filmorate.entity.film.Mpa;
import ru.yandex.practicum.filmorate.entity.user.User;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class FilmDaoTest {

    private final FilmDao filmDao;
    private final UserDao userDao;

    @BeforeEach
    public void beforeEach() {
        filmDao.addFilm(Film.builder()
                .name("name1")
                .description("description1")
                .releaseDate(LocalDate.of(2000,1,2))
                .duration(100)
                .rate(2)
                .mpa(Mpa.builder().id(1).build())
                .build());
        filmDao.addFilm(Film.builder()
                .name("name2")
                .description("description2")
                .releaseDate(LocalDate.of(2020,4,5))
                .duration(120)
                .rate(3)
                .mpa(Mpa.builder().id(2).build())
                .build());
        userDao.addUser(User.builder()
                .email("name1@mail.com")
                .login("login1")
                .name("name1")
                .birthday(LocalDate.of(1999, 2, 3))
                .build());
        userDao.addUser(User.builder()
                .email("name2@mail.com")
                .login("login2")
                .name("name2")
                .birthday(LocalDate.of(1997, 3, 7))
                .build());
    }

    @Test
    @DirtiesContext
    public void getFilmById() {
        Optional<Film> film = filmDao.getFilmById(2L);
        assertNotNull(film);
        assertTrue(film.isPresent());
        assertEquals("name2", film.get().getName());
        assertEquals("description2", film.get().getDescription());
        assertEquals(120, film.get().getDuration());

        Optional<Film> fakeFilm = filmDao.getFilmById(99L);
        assertNotNull(fakeFilm);
        assertTrue(fakeFilm.isEmpty());
    }

    @Test
    @DirtiesContext
    public void addFilm() {
        Long id = filmDao.addFilm(Film.builder()
                .name("name3")
                .description("description3")
                .releaseDate(LocalDate.now())
                .duration(90)
                .rate(4)
                .mpa(Mpa.builder().id(3).build())
                .build());
        assertNotNull(id);
        Optional<Film> addedFilm = filmDao.getFilmById(id);
        assertNotNull(addedFilm);
        assertTrue(addedFilm.isPresent());
        assertEquals(3, addedFilm.get().getId());
    }

    @Test
    @DirtiesContext
    public void getAllFilm() {
        List<Film> films = filmDao.getFilms();
        assertNotNull(films);
        assertEquals(2, films.size());
    }

    @Test
    @DirtiesContext
    public void updateFilm() {
        filmDao.updateFilm(Film.builder()
                .id(1L)
                .name("newFilmName1")
                .description("newDescription1")
                .releaseDate(LocalDate.now())
                .duration(120)
                .mpa(Mpa.builder().id(5).build())
                .build());
        Optional<Film> updatedFilm = filmDao.getFilmById(1L);
        assertNotNull(updatedFilm);
        assertTrue(updatedFilm.isPresent());
        assertEquals("newFilmName1", updatedFilm.get().getName());
        assertEquals("newDescription1", updatedFilm.get().getDescription());
        assertEquals(120, updatedFilm.get().getDuration());
        assertEquals("NC-17", updatedFilm.get().getMpa().getName());
    }

    @Test
    @DirtiesContext
    public void getPopularFilms() {
        List<Film> popularFilms = filmDao.getPopularFilms(1);
        assertNotNull(popularFilms);
        assertEquals(1, popularFilms.size());
        assertEquals(1, popularFilms.get(0).getId());
        List<Film> popularFilms1 = filmDao.getPopularFilms(2);
        assertNotNull(popularFilms1);
        assertEquals(2, popularFilms1.size());
        assertEquals(2, popularFilms1.get(1).getId());
    }

    @Test
    @DirtiesContext
    public void addLike() {
        List<Film> popularFilms1 = filmDao.getPopularFilms(1);
        assertNotNull(popularFilms1);
        filmDao.addLike(1L, 1L);
        filmDao.addLike(2L, 1L);
        filmDao.addLike(2L, 2L);
        List<Film> popularFilms2 = filmDao.getPopularFilms(2);
        assertNotNull(popularFilms2);
        assertEquals(2, popularFilms2.size());
        assertEquals(2, popularFilms2.get(0).getId());
        assertEquals(2, popularFilms2.get(0).getRate());
    }

    @Test
    @DirtiesContext
    public void deleteLike() {
        List<Film> popularFilms1 = filmDao.getPopularFilms(1);
        assertNotNull(popularFilms1);
        assertEquals(1, popularFilms1.size());
        filmDao.addLike(1L, 1L);
        filmDao.addLike(2L, 1L);
        filmDao.addLike(2L, 2L);
        List<Film> popularFilms2 = filmDao.getPopularFilms(2);
        assertNotNull(popularFilms2);
        assertEquals(2, popularFilms2.size());
        assertEquals(2, popularFilms2.get(0).getId());
        assertEquals(2, popularFilms2.get(0).getRate());
        filmDao.deleteLike(2L, 2L);
        filmDao.deleteLike(2L, 1L);
        List<Film> popularFilms3 = filmDao.getPopularFilms(1);
        assertNotNull(popularFilms2);
        assertEquals(1, popularFilms3.size());
        assertEquals(1, popularFilms3.get(0).getId());
        assertEquals(1, popularFilms3.get(0).getRate());
    }

    @Test
    @DirtiesContext
    public void getFilmsLikedByUser() {
        filmDao.addLike(1L, 1L);
        filmDao.addLike(2L, 1L);
        List<Film> likeFilms = filmDao.getFilmsLikedByUser(1L);
        assertNotNull(likeFilms);
        assertEquals(2, likeFilms.size());
        assertEquals(1, likeFilms.get(0).getId());
    }

    @Test
    @DirtiesContext
    public void findAllFilmLikes() {
        filmDao.addLike(1L, 1L);
        filmDao.addLike(2L, 2L);
        List<FilmLikes> filmLikes = filmDao.findAllFilmLikes();
        assertNotNull(filmLikes);
        assertEquals(2, filmLikes.size());
        assertEquals(1, filmLikes.get(0).getFilmId());
        assertEquals(1, filmLikes.get(0).getUserId());
        assertEquals(2, filmLikes.get(1).getFilmId());
        assertEquals(2, filmLikes.get(1).getUserId());
    }

    @Test
    @DirtiesContext
    public void deleteFilmById() {
        filmDao.deleteFilmByFilmId(1L);
        List<Film> films = filmDao.getFilms();
        assertNotNull(films);
        assertEquals(1, films.size());
        assertEquals(2L, films.get(0).getId());
    }
}
