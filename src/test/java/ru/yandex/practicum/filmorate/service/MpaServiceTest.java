package ru.yandex.practicum.filmorate.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import ru.yandex.practicum.filmorate.dao.MpaDao;
import ru.yandex.practicum.filmorate.entity.film.Mpa;
import ru.yandex.practicum.filmorate.exception.MpaNotFoundException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class MpaServiceTest {
    @Mock
    private MpaDao mpaDao;

    @InjectMocks
    private MpaService mpaService;

    @Test
    public void getMpaById() {
        Optional<Mpa> optionalMpa = Optional.of(Mpa.builder()
                .id(1)
                .name("G")
                .build());
        when(mpaDao.getMpaById(Mockito.anyInt())).thenReturn(optionalMpa);
        Mpa mpa = mpaService.getMpaById(1);
        assertNotNull(mpa);
        assertEquals(1, mpa.getId());
        assertEquals("G", mpa.getName());

        when(mpaDao.getMpaById(Mockito.anyInt())).thenReturn(Optional.empty());
        assertThrows(MpaNotFoundException.class, () -> mpaService.getMpaById(1));
    }

    @Test
    public void getAllMpa() {
        List<Mpa> mpaList = List.of(Mpa.builder()
                        .id(1)
                        .build(),
                Mpa.builder()
                        .id(2)
                        .build());
        when(mpaDao.getAllMpa()).thenReturn(mpaList);
        List<Mpa> mpaList1 = mpaService.getAllMpa();
        assertNotNull(mpaList1);
        assertEquals(2, mpaList1.size());
        assertEquals(1, mpaList1.get(0).getId());
        assertEquals(2, mpaList1.get(1).getId());
    }
}
