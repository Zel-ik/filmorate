package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.entity.film.Director;
import ru.yandex.practicum.filmorate.exception.DirectorNotFoundException;
import ru.yandex.practicum.filmorate.service.DirectorService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = DirectorController.class)
public class DirectorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DirectorService directorService;

    @Autowired
    private ObjectMapper objectMapper;

    @SneakyThrows
    @Test
    public void findAllDirectors_thenReturnOk() {
        List<Director> directors = List.of(Director.builder().id(2L).name("name").build());
        when(directorService.getDirectors()).thenReturn(directors);
        String response = mockMvc.perform(get("/directors")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        verify(directorService, atLeast(1)).getDirectors();
        assertEquals(response, objectMapper.writeValueAsString(directors));
    }

    @SneakyThrows
    @Test
    public void getDirector_whenInvokedWithValidId_thenReturnOk() {
        Director director = Director.builder().id(2L).name("name").build();
        when(directorService.getDirector(2L)).thenReturn(director);
        String response = mockMvc.perform(get("/directors/2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        verify(directorService, atLeast(1)).getDirector(2L);
        assertEquals(response, objectMapper.writeValueAsString(director));
    }

    @SneakyThrows
    @Test
    public void getDirector_whenInvokedWithInvalidId_thenReturnNotFound() {
        when(directorService.getDirector(2L)).thenThrow(DirectorNotFoundException.class);
        mockMvc.perform(get("/directors/2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andReturn()
                .getResponse()
                .getContentAsString();
        verify(directorService, atLeast(1)).getDirector(2L);
    }

    @SneakyThrows
    @Test
    public void addDirector_thenReturnOk() {
        Director director = Director.builder().id(2L).name("name").build();
        when(directorService.addDirector(director)).thenReturn(director);
        String response = mockMvc.perform(post("/directors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(director)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        verify(directorService, atLeast(1)).addDirector(director);
        assertEquals(response, objectMapper.writeValueAsString(director));
    }

    @SneakyThrows
    @Test
    public void updateDirector_whenInvokedWithValidId_thenReturnOk() {
        Director director = Director.builder().id(2L).name("name").build();
        when(directorService.updateDirector(director)).thenReturn(director);
        String response = mockMvc.perform(put("/directors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(director)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        verify(directorService, atLeast(1)).updateDirector(director);
        assertEquals(response, objectMapper.writeValueAsString(director));
    }

    @SneakyThrows
    @Test
    public void updateDirector_whenInvokedWithInvalidId_thenReturnNotFound() {
        Director director = Director.builder().id(2L).name("name").build();
        when(directorService.updateDirector(director)).thenThrow(DirectorNotFoundException.class);
        mockMvc.perform(put("/directors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(director)))
                .andExpect(status().isNotFound())
                .andReturn()
                .getResponse()
                .getContentAsString();
        verify(directorService, atLeast(1)).updateDirector(director);
    }

    @SneakyThrows
    @Test
    public void deleteDirector_whenInvokedWithValidId_thenReturnOk() {
        doNothing().when(directorService).deleteDirector(3L);
        mockMvc.perform(delete("/directors/3"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        verify(directorService, atLeast(1)).deleteDirector(3L);
    }
}
