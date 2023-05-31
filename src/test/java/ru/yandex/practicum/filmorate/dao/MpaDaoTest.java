package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.entity.film.Mpa;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class MpaDaoTest {

    private final MpaDao mpaDao;

    @Test
    @DirtiesContext
    public void getMpaList() {
        List<Mpa> mpaList = mpaDao.getAllMpa();
        assertNotNull(mpaList);
        assertEquals(5, mpaList.size());
    }

    @Test
    @DirtiesContext
    public void getMpaById() {
        Optional<Mpa> mpa = mpaDao.getMpaById(3);
        assertNotNull(mpa);
        assertTrue(mpa.isPresent());
        assertEquals("PG-13", mpa.get().getName().strip());

        Optional<Mpa> fakeMpa = mpaDao.getMpaById(99);
        assertNotNull(fakeMpa);
        assertTrue(fakeMpa.isEmpty());
    }
}
