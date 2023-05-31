package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.entity.film.Genre;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class GenreDaoTest {

    private final GenreDao genreDao;

    @Test
    @DirtiesContext
    public void getGenreList() {
        List<Genre> genreList = genreDao.getAllGenres();
        assertNotNull(genreList);
        assertEquals(6, genreList.size());
    }

    @Test
    @DirtiesContext
    public void getGenreById() {
        Optional<Genre> genre = genreDao.getGenreById(2);
        assertNotNull(genre);
        assertTrue(genre.isPresent());
        assertEquals("Драма", genre.get().getName().strip());

        Optional<Genre> fakeGenre = genreDao.getGenreById(99);
        assertNotNull(fakeGenre);
        assertTrue(fakeGenre.isEmpty());
    }
}
