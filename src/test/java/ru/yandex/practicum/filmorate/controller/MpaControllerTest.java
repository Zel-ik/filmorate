package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.entity.film.Mpa;
import ru.yandex.practicum.filmorate.exception.MpaNotFoundException;
import ru.yandex.practicum.filmorate.service.MpaService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = MpaController.class)
public class MpaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MpaService mpaService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @SneakyThrows
    public void getAllMpa_thenReturnOkWithMpaList() {
        List<Mpa> mpaList = List.of(Mpa.builder()
                .id(1)
                .name("G")
                .build()
        );
        when(mpaService.getAllMpa()).thenReturn(mpaList);
        String response = mockMvc.perform(get("/mpa")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        verify(mpaService, atLeast(1)).getAllMpa();
        assertEquals(response, objectMapper.writeValueAsString(mpaList));
    }

    @Test
    @SneakyThrows
    public void getMpaById_whenInvokedWithValidId_thenReturnOkWithMpa() {
        Mpa mpa = Mpa.builder()
                .id(1)
                .name("G")
                .build();
        when(mpaService.getMpaById(1)).thenReturn(mpa);
        String response = mockMvc.perform(get("/mpa/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        verify(mpaService, atLeast(1)).getMpaById(1);
        assertEquals(response, objectMapper.writeValueAsString(mpa));
    }

    @Test
    @SneakyThrows
    public void getMpaById_whenInvokedWithInvalidId_thenReturnNotFoundWithMpa() {
        when(mpaService.getMpaById(99)).thenThrow(MpaNotFoundException.class);
        mockMvc.perform(get("/mpa/99")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andReturn()
                .getResponse()
                .getContentAsString();
        verify(mpaService, atLeast(1)).getMpaById(99);
    }
}
