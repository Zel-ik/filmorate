package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.entity.film.Director;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class DirectorDaoTest {

    private final DirectorDao directorDao;

    @BeforeEach
    public void beforeEach() {
        directorDao.addDirector(Director.builder().name("name1").build());
        directorDao.addDirector(Director.builder().name("name2").build());
    }

    @Test
    @DirtiesContext
    public void getDirectorById() {
        Optional<Director> director = directorDao.getDirectorById(2L);
        assertNotNull(director);
        assertTrue(director.isPresent());
        assertEquals(2, director.get().getId());
        assertEquals("name2", director.get().getName());
    }

    @Test
    @DirtiesContext
    public void getDirectors() {
        List<Director> directors = directorDao.getAllDirector();
        assertNotNull(directors);
        assertEquals(2, directors.size());
        assertEquals(1, directors.get(0).getId());
        assertEquals(2, directors.get(1).getId());
    }

    @Test
    @DirtiesContext
    public void addDirector() {
        directorDao.addDirector(Director.builder().name("name3").build());
        List<Director> directors = directorDao.getAllDirector();
        assertNotNull(directors);
        assertEquals(3, directors.size());
    }

    @Test
    @DirtiesContext
    public void updateDirector() {
        directorDao.updateDirector(Director.builder().id(1L).name("updated").build());
        Optional<Director> director = directorDao.getDirectorById(1L);
        assertTrue(director.isPresent());
        assertEquals("updated", director.get().getName());
    }

    @Test
    @DirtiesContext
    public void deleteDirector() {
        directorDao.deleteDirector(1L);
        List<Director> directors = directorDao.getAllDirector();
        assertNotNull(directors);
        assertEquals(1, directors.size());
        assertEquals(2, directors.get(0).getId());
    }
}
