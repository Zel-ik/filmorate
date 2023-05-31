package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.entity.Event;
import ru.yandex.practicum.filmorate.entity.EventType;
import ru.yandex.practicum.filmorate.entity.Operation;
import ru.yandex.practicum.filmorate.entity.film.Film;
import ru.yandex.practicum.filmorate.entity.user.User;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.service.EventService;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private EventService eventService;

    @Autowired
    private ObjectMapper objectMapper;

    @SneakyThrows
    @Test
    public void getAllUsers_ReturnOkWithListOfUsers() {
        List<User> users = List.of(User.builder()
                        .id(1L)
                        .email("name1@mail.com")
                        .login("login1")
                        .name("name1")
                        .build(),
                User.builder()
                        .id(2L)
                        .email("name2@mail.com")
                        .login("login2")
                        .name("name2")
                        .build()
        );
        when(userService.getUsers()).thenReturn(users);
        String response = mockMvc.perform(get("/users")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        verify(userService, atLeast(1)).getUsers();
        Assertions.assertEquals(response, objectMapper.writeValueAsString(users));
    }

    @SneakyThrows
    @Test
    public void getUser_whenInvokedWithValidId_thenReturnOkWithUser() {
        User user = User.builder()
                .id(1L)
                .email("name1@mail.com")
                .login("login1")
                .name("name1")
                .build();
        when(userService.getUser(1L)).thenReturn(user);
        String response = mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        verify(userService, atLeast(1)).getUser(1L);
        Assertions.assertEquals(response, objectMapper.writeValueAsString(user));
    }

    @SneakyThrows
    @Test
    public void getUser_whenInvokedWithInvalidId_thenReturnOkWithUser() {
        when(userService.getUser(99L)).thenThrow(UserNotFoundException.class);
        mockMvc.perform(get("/users/99"))
                .andExpect(status().isNotFound())
                .andReturn()
                .getResponse()
                .getContentAsString();
        verify(userService, atLeast(1)).getUser(99L);
    }

    @SneakyThrows
    @Test
    public void getCommonFriends_thenReturnOkWithCommonFriendsList() {
        List<User> commonFriends = List.of(User.builder()
                .id(1L)
                .email("name1@mail.com")
                .login("login1")
                .name("name1")
                .build());
        when(userService.getCommonFriends(1L, 2L)).thenReturn(commonFriends);
        String response = mockMvc.perform(get("/users/1/friends/common/2"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        verify(userService, atLeast(1)).getCommonFriends(1L, 2L);
        Assertions.assertEquals(response, objectMapper.writeValueAsString(commonFriends));
    }

    @SneakyThrows
    @Test
    public void getUserFriends_thenReturnOkWithFriendsList() {
        List<User> friendsList = List.of(User.builder()
                .id(1L)
                .email("name1@mail.com")
                .login("login1")
                .name("name1")
                .build());
        when(userService.getUserFriends(1L)).thenReturn(friendsList);
        String response = mockMvc.perform(get("/users/1/friends/"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        verify(userService, atLeast(1)).getUserFriends(1L);
        Assertions.assertEquals(response, objectMapper.writeValueAsString(friendsList));
    }

    @SneakyThrows
    @Test
    public void addUser_thenReturnOkWithAddedUser() {
        User user = User.builder()
                .id(1L)
                .email("name1@mail.com")
                .login("login1")
                .name("name1")
                .build();
        when(userService.addUser(user)).thenReturn(user);
        String response = mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        verify(userService, atLeast(1)).addUser(user);
        Assertions.assertEquals(response, objectMapper.writeValueAsString(user));
    }

    @SneakyThrows
    @Test
    public void updateUser_thenReturnOkWithUpdatedUser() {
        User user = User.builder()
                .id(1L)
                .email("name1@mail.com")
                .login("login1")
                .name("name1")
                .build();
        when(userService.updateUser(user)).thenReturn(user);
        String response = mockMvc.perform(put("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        verify(userService, atLeast(1)).updateUser(user);
        Assertions.assertEquals(response, objectMapper.writeValueAsString(user));
    }

    @SneakyThrows
    @Test
    public void updateUser_thenReturnStatusNotFound() {
        User user = User.builder()
                .id(1L)
                .email("name1@mail.com")
                .login("login1")
                .name("name1")
                .build();
        when(userService.updateUser(user)).thenThrow(UserNotFoundException.class);
        mockMvc.perform(put("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isNotFound())
                .andReturn()
                .getResponse()
                .getContentAsString();
        verify(userService, atLeast(1)).updateUser(user);
    }

    @SneakyThrows
    @Test
    public void addFriend_whenInvokedWithValidUsers_thenReturnOkWithUser() {
        User user = User.builder()
                .id(1L)
                .email("name1@mail.com")
                .login("login1")
                .name("name1")
                .build();
        when(userService.addFriend(1L, 2L)).thenReturn(user);
        String response = mockMvc.perform(put("/users/1/friends/2"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        verify(userService, atLeast(1)).addFriend(1L, 2L);
        Assertions.assertEquals(response, objectMapper.writeValueAsString(user));
    }

    @SneakyThrows
    @Test
    public void addFriend_whEnInvokedWithInvalidUsers_thenReturnOkWithUser() {
        when(userService.addFriend(1L, 2L)).thenThrow(UserNotFoundException.class);
        mockMvc.perform(put("/users/1/friends/2"))
                .andExpect(status().isNotFound())
                .andReturn()
                .getResponse()
                .getContentAsString();
        verify(userService, atLeast(1)).addFriend(1L, 2L);
    }


    @SneakyThrows
    @Test
    public void deleteUserFriend_whenInvokedWithValidUsers_thenReturnOkWithUser() {
        User user = User.builder()
                .id(1L)
                .email("name1@mail.com")
                .login("login1")
                .name("name1")
                .build();
        when(userService.deleteFriend(1L, 3L)).thenReturn(user);
        String response = mockMvc.perform(delete("/users/1/friends/3"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        verify(userService, atLeast(1)).deleteFriend(1L, 3L);
        Assertions.assertEquals(response, objectMapper.writeValueAsString(user));
    }

    @SneakyThrows
    @Test
    public void deleteUserFriend_whenInvokedWithInvalidUsers_thenReturnOkWithUser() {
        when(userService.deleteFriend(1L, 3L)).thenThrow(UserNotFoundException.class);
        mockMvc.perform(delete("/users/1/friends/3"))
                .andExpect(status().isNotFound())
                .andReturn()
                .getResponse()
                .getContentAsString();
        verify(userService, atLeast(1)).deleteFriend(1L, 3L);
    }

    @SneakyThrows
    @Test
    public void getUserFeed_ReturnOkWithFeed() {
        List<Event> events = List.of(new Event(1L,
                1L,
                EventType.FRIEND,
                Operation.ADD,
                1682812242667L,
                2L));
        when(eventService.getUserFeed(1L)).thenReturn(events);
        String response = mockMvc.perform(get("/users/1/feed")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        verify(eventService, atLeast(1)).getUserFeed(1L);
        Assertions.assertEquals(response, objectMapper.writeValueAsString(events));
    }

    @SneakyThrows
    @Test
    public void getRecommendedFilms_whenInvokedWithValidId_thenReturnOkWithFilmList() {
        List<Film> recommendedFilms = List.of(Film.builder().id(1L).name("name").build());
        when(userService.getRecommendedFilms(Mockito.anyLong())).thenReturn(recommendedFilms);
        String response = mockMvc.perform(get("/users/1/recommendations"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        verify(userService, atLeast(1)).getRecommendedFilms(Mockito.anyLong());
        Assertions.assertEquals(response, objectMapper.writeValueAsString(recommendedFilms));
    }

    @SneakyThrows
    @Test
    public void getRecommendedFilms_whenInvokedWithInvalidUserId_thenReturnNotFound() {
        when(userService.getRecommendedFilms(Mockito.anyLong())).thenThrow(UserNotFoundException.class);
        mockMvc.perform(get("/users/1/recommendations"))
                .andExpect(status().isNotFound())
                .andReturn()
                .getResponse()
                .getContentAsString();
        verify(userService, atLeast(1)).getRecommendedFilms(Mockito.anyLong());
    }

    @SneakyThrows
    @Test
    public void deleteUser_whenInvokedWithValidUserId_thenReturnOk() {
        doNothing().when(userService).deleteUserById(Mockito.anyLong());
        mockMvc.perform(delete("/users/4"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        verify(userService, atLeast(1)).deleteUserById(Mockito.anyLong());
    }
}
