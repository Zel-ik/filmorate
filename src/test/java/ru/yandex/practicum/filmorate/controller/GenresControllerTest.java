package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.entity.film.Genre;
import ru.yandex.practicum.filmorate.exception.GenreNotFoundException;
import ru.yandex.practicum.filmorate.service.GenreService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@JsonInclude
@WebMvcTest(controllers = GenresController.class)
public class GenresControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GenreService genreService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @SneakyThrows
    public void getAllGenres_thenReturnOkWithGenreList() {
        List<Genre> genreList = List.of(Genre.builder()
                .id(2)
                .name("Comedy")
                .build()
        );
        when(genreService.getAllGenres()).thenReturn(genreList);
        String response = mockMvc.perform(get("/genres")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        verify(genreService, atLeast(1)).getAllGenres();
        assertEquals(response, objectMapper.writeValueAsString(genreList));
    }

    @Test
    @SneakyThrows
    public void getGenreById_whenInvokedWithValidId_thenReturnOkWithGenre() {
        Genre genre = Genre.builder()
                .id(2)
                .name("Comedy")
                .build();
        when(genreService.getGenreById(2)).thenReturn(genre);
        String response = mockMvc.perform(get("/genres/2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        verify(genreService, atLeast(1)).getGenreById(2);
        assertEquals(response, objectMapper.writeValueAsString(genre));
    }

    @Test
    @SneakyThrows
    public void getGenreById_whenInvokedWithInvalidId_thenReturnNotFound() {
        when(genreService.getGenreById(99)).thenThrow(GenreNotFoundException.class);
        String response = mockMvc.perform(get("/genres/99")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andReturn()
                .getResponse()
                .getContentAsString();
        verify(genreService, atLeast(1)).getGenreById(99);
    }
}
