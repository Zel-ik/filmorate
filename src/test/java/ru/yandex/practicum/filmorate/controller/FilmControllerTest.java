package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.entity.film.Film;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.IncorrectParameterException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = FilmController.class)
class FilmControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FilmService filmService;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @SneakyThrows
    @Test
    public void getAllFilms_thenReturnOkWithAllFilms() {
        List<Film> films = List.of(Film.builder()
                .id(1L)
                .name("name1")
                .description("description1")
                .releaseDate(LocalDate.now())
                .duration(100)
                .build()
        );
        when(filmService.getFilms()).thenReturn(films);
        String response = mockMvc.perform(get("/films")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        verify(filmService, atLeast(1)).getFilms();
        assertEquals(response, objectMapper.writeValueAsString(films));
    }

    @SneakyThrows
    @Test
    public void getFilm_whenInvokedWithValidId_thenReturnOkWithFilm() {
        Film film = Film.builder()
                .id(1L)
                .name("name1")
                .description("description1")
                .releaseDate(LocalDate.now())
                .duration(100)
                .build();
        when(filmService.getFilm(1L)).thenReturn(film);
        String response = mockMvc.perform(get("/films/1"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        verify(filmService, atLeast(1)).getFilm(1L);
        assertEquals(response, objectMapper.writeValueAsString(film));
    }

    @SneakyThrows
    @Test
    public void getFilm_whenInvokedWithInvalidId_thenReturnNotFound() {
        when((filmService.getFilm(999L))).thenThrow(FilmNotFoundException.class);
        mockMvc.perform(get("/films/999"))
                .andExpect(status().is(404))
                .andReturn()
                .getResponse();
        verify(filmService, atLeast(1)).getFilm(999L);
    }

    @SneakyThrows
    @Test
    public void getMostLikedFilms_thenReturnOkWithFilms() {
        Film film = Film.builder()
                .id(1L)
                .name("name1")
                .description("description1")
                .releaseDate(LocalDate.now())
                .duration(100)
                .build();
        List<Film> films = List.of(film);
        when(filmService.getPopularFilms(0, 9999,1)).thenReturn(films);
        String response = mockMvc.perform(get("/films/popular?count=1&genreId=0&year=9999"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        verify(filmService, atLeast(1)).getPopularFilms(0, 9999, 1);
        assertEquals(response, objectMapper.writeValueAsString(films));
    }

    @SneakyThrows
    @Test
    public void getMostLikedFilms_whenInvokedWithInvalidParameter_thenReturnStatusBadRequest() {
        when(filmService.getPopularFilms(null, null, 0)).thenThrow(new IncorrectParameterException("count"));
        mockMvc.perform(get("/films/popular?count=0"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse();
        verify(filmService, atLeast(1)).getPopularFilms(null, null, 0);
    }

    @SneakyThrows
    @Test
    public void addFilm_thenReturnOkWithAddedFilm() {
        Film film = Film.builder()
                .id(1L)
                .name("name1")
                .description("description1")
                .releaseDate(LocalDate.now())
                .duration(100)
                .build();
        when(filmService.addFilm(film)).thenReturn(film);
        String response = mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(film)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        verify(filmService, atLeast(1)).addFilm(film);
        assertEquals(response, objectMapper.writeValueAsString(film));
    }

    @SneakyThrows
    @Test
    public void updateFilm_thenReturnOkWithUpdatedFilm() {
        Film film = Film.builder()
                .id(1L)
                .name("name1")
                .description("description1")
                .releaseDate(LocalDate.now())
                .duration(100)
                .build();
        when(filmService.updateFilm(film)).thenReturn(film);
        String response = mockMvc.perform(put("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(film)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        verify(filmService, atLeast(1)).updateFilm(film);
        assertEquals(response, objectMapper.writeValueAsString(film));
    }

    @SneakyThrows
    @Test
    public void updateFilm_whenInvokedWithInvalidId_thenReturnStatusNotFound() {
        Film film = Film.builder()
                .id(1L)
                .name("name1")
                .description("description1")
                .releaseDate(LocalDate.now())
                .duration(100)
                .build();
        when(filmService.updateFilm(film)).thenThrow(FilmNotFoundException.class);
        mockMvc.perform(put("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(film)))
                .andExpect(status().isNotFound())
                .andReturn()
                .getResponse()
                .getContentAsString();
        verify(filmService, atLeast(1)).updateFilm(film);
    }

    @SneakyThrows
    @Test
    public void addLike_whenInvokedWithValidIds_thenReturnOkWithFilm() {
        Film film = Film.builder()
                .id(1L)
                .name("name1")
                .description("description1")
                .releaseDate(LocalDate.now())
                .duration(100)
                .build();
        when(filmService.addLike(1L, 9L)).thenReturn(film);
        String response = mockMvc.perform(put("/films/1/like/9"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        verify(filmService, atLeast(1)).addLike(1L, 9L);
        assertEquals(response, objectMapper.writeValueAsString(film));
    }

    @SneakyThrows
    @Test
    public void addLike_whenInvokedWithInvalidFilm_thenReturnNotFound() {
        when(filmService.addLike(1L, 9L)).thenThrow(FilmNotFoundException.class);
        mockMvc.perform(put("/films/1/like/9"))
                .andExpect(status().isNotFound())
                .andReturn()
                .getResponse()
                .getContentAsString();
        verify(filmService, atLeast(1)).addLike(1L, 9L);
    }

    @SneakyThrows
    @Test
    public void addLike_whenInvokedWithInvalidUser_thenReturnNotFound() {
        when(filmService.addLike(1L, 9L)).thenThrow(UserNotFoundException.class);
        mockMvc.perform(put("/films/1/like/9"))
                .andExpect(status().isNotFound())
                .andReturn()
                .getResponse()
                .getContentAsString();
        verify(filmService, atLeast(1)).addLike(1L, 9L);
    }

    @SneakyThrows
    @Test
    public void deleteLike_whenInvokedWithValidIds_thenReturnOkWithFilm() {
        Film film = Film.builder()
                .id(1L)
                .name("name1")
                .description("description1")
                .releaseDate(LocalDate.now())
                .duration(100)
                .build();
        when(filmService.deleteLike(1L, 9L)).thenReturn(film);
        String response = mockMvc.perform(delete("/films/1/like/9"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        verify(filmService, atLeast(1)).deleteLike(1L, 9L);
        assertEquals(response, objectMapper.writeValueAsString(film));
    }

    @SneakyThrows
    @Test
    public void deleteLike_whenInvokedWithInvalidFilm_thenReturnNotFound() {
        when(filmService.deleteLike(1L, 9L)).thenThrow(FilmNotFoundException.class);
        mockMvc.perform(delete("/films/1/like/9"))
                .andExpect(status().isNotFound())
                .andReturn()
                .getResponse()
                .getContentAsString();
        verify(filmService, atLeast(1)).deleteLike(1L, 9L);
    }

    @SneakyThrows
    @Test
    public void deleteLike_whenInvokedWithInvalidUser_thenReturnNotFound() {
        when(filmService.deleteLike(1L, 9L)).thenThrow(UserNotFoundException.class);
        mockMvc.perform(delete("/films/1/like/9"))
                .andExpect(status().isNotFound())
                .andReturn()
                .getResponse()
                .getContentAsString();
        verify(filmService, atLeast(1)).deleteLike(1L, 9L);
    }

    @SneakyThrows
    @Test
    public void getCommonFilms_whenInvokedWithValidUserIds_thenReturnOk() {
        List<Film> commonFilms = List.of(Film.builder()
                .id(3)
                .name("name")
                .description("description")
                .build());
        when(filmService.getCommonFilms(4L, 5L)).thenReturn(commonFilms);
        String response = mockMvc.perform(get("/films/common?userId=4&friendId=5"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        verify(filmService, atLeast(1)).getCommonFilms(4L, 5L);
        assertEquals(response, objectMapper.writeValueAsString(commonFilms));
    }

    @SneakyThrows
    @Test
    public void getCommonFilms_whenInvokedWithInvalidUserIds_thenReturnNotFound() {
        when(filmService.getCommonFilms(4L, 5L)).thenThrow(UserNotFoundException.class);
        mockMvc.perform(get("/films/common?userId=4&friendId=5"))
                .andExpect(status().isNotFound())
                .andReturn()
                .getResponse()
                .getContentAsString();
        verify(filmService, atLeast(1)).getCommonFilms(4L, 5L);
    }

    @SneakyThrows
    @Test
    public void deleteFilmById_whenInvokedWithValidFilmId_thenReturnOk() {
        doNothing().when(filmService).deleteFilmById(Mockito.anyLong());
        mockMvc.perform(delete("/films/1"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        verify(filmService, atLeast(1)).deleteFilmById(Mockito.anyLong());
    }
}
