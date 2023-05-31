package ru.yandex.practicum.filmorate.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import ru.yandex.practicum.filmorate.dao.FilmDao;
import ru.yandex.practicum.filmorate.dao.UserDao;
import ru.yandex.practicum.filmorate.entity.film.Film;
import ru.yandex.practicum.filmorate.entity.film.FilmLikes;
import ru.yandex.practicum.filmorate.entity.user.User;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserDao userDao;

    @Mock
    private FilmDao filmDao;

    @Test
    public void addUser() {
        User user = User.builder()
                .email("name@mail.com")
                .login("login")
                .name("name")
                .birthday(LocalDate.of(2000, 3, 20))
                .build();
        Optional<User> userOptional = Optional.of(User.builder()
                .id(1L)
                .email("name@mail.com")
                .login("login")
                .name("name")
                .birthday(LocalDate.of(2000, 3, 20))
                .build());
        when(userDao.getUserById(Mockito.any(Long.class))).thenReturn(userOptional);
        User addedUser = userService.addUser(user);
        assertNotNull(addedUser);
        assertEquals(1L, addedUser.getId());
        when(userDao.getUserById(Mockito.any(Long.class))).thenReturn(Optional.empty());
        assertThrows((UserNotFoundException.class), () -> userService.addUser(user));
    }

    @Test
    public void updateUser() {
        User user = User.builder()
                .id(1L)
                .email("name1@mail.com")
                .login("login1")
                .birthday(LocalDate.of(2000, 3, 20))
                .name("name1")
                .build();
        Optional<User> optionalUser = Optional.of(User.builder()
                .id(1L)
                .email("newName1@mail.com")
                .login("newLogin1")
                .name("newName1")
                .birthday(LocalDate.of(2000, 3, 20))
                .build());
        when(userDao.getUserById(Mockito.any(Long.class))).thenReturn(optionalUser);
        User updatedUser = userService.updateUser(user);
        assertNotNull(updatedUser);
        assertEquals("newName1", updatedUser.getName());
        assertEquals("newLogin1", updatedUser.getLogin());
        assertEquals("newName1@mail.com", updatedUser.getEmail());

        when(userDao.getUserById(Mockito.any(Long.class))).thenReturn(Optional.empty());
        assertThrows((UserNotFoundException.class), () -> userService.updateUser(user));
    }

    @Test
    public void getUser() {
        Optional<User> optionalUser = Optional.of(User.builder()
                .id(1L)
                .email("newName1@mail.com")
                .login("newLogin1")
                .name("newName1")
                .birthday(LocalDate.of(2000, 3, 20))
                .build());
        when(userDao.getUserById(Mockito.anyLong())).thenReturn(optionalUser);
        User user = userService.getUser(1L);
        assertNotNull(user);
        assertEquals(user, optionalUser.get());

        when(userDao.getUserById(Mockito.anyLong())).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> userService.getUser(1L));
    }

    @Test
    public void getUsers() {
        List<User> returnedUsers = List.of(User.builder()
                .id(1L)
                .email("name@mail.com")
                .login("login")
                .name("name")
                .birthday(LocalDate.of(2000, 3, 20))
                .build()
        );
        when(userDao.getUsers()).thenReturn(returnedUsers);
        List<User> users = userService.getUsers();
        assertNotNull(users);
        assertEquals(1, users.size());
    }

    @Test
    public void getUsersFriends() {
        User user1 = User.builder()
                .id(1)
                .build();
        User user2 = User.builder()
                .id(2)
                .build();
        User user3 = User.builder()
                .id(3)
                .build();
        Set<User> userFriends = Set.of(user2, user3);
        when(userDao.getUserById(Mockito.anyLong())).thenReturn(Optional.of(user1));
        when(userDao.getUserFriends(Mockito.anyLong())).thenReturn(userFriends);
        List<User> users = userService.getUserFriends(1L);
        assertNotNull(users);
        assertEquals(2, users.size());
        assertTrue(users.contains(user2));
        assertTrue(users.contains(user3));

        when(userDao.getUserById(Mockito.anyLong())).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> userService.getUser(1L));
        assertThrows(UserNotFoundException.class, () -> userService.getUserFriends(1L));
    }

    @Test
    public void getCommonFriends() {
        User user1 = User.builder()
                .id(1L)
                .build();
        User user2 = User.builder()
                .id(2L)
                .build();
        User user3 = User.builder()
                .id(3L)
                .build();
        User user4 = User.builder()
                .id(4L)
                .build();
        Set<User> user1Friends = Set.of(user2, user3, user4);
        Set<User> user4Friends = Set.of(user1, user2);
        when(userDao.getUserById(Mockito.anyLong())).thenReturn(Optional.of(user1));
        when(userDao.getUserById(Mockito.anyLong())).thenReturn(Optional.of(user2));
        when(userDao.getUserFriends(1L)).thenReturn(user1Friends);
        when(userDao.getUserFriends(4L)).thenReturn(user4Friends);
        List<User> commonFriends = userService.getCommonFriends(1L, 4L);
        assertNotNull(commonFriends);
        assertEquals(1, commonFriends.size());
        assertTrue(commonFriends.contains(user2));

        when(userDao.getUserById(Mockito.anyLong())).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> userService.getCommonFriends(1L, 4L));
    }

    @Test
    public void getRecommendedFilms() {
        List<FilmLikes> filmLikes = List.of(FilmLikes.builder().filmId(1L).userId(1L).build(),
                FilmLikes.builder().filmId(2L).userId(1L).build(),
                FilmLikes.builder().filmId(2L).userId(2L).build(),
                FilmLikes.builder().filmId(3L).userId(2L).build());
        when(filmDao.findAllFilmLikes()).thenReturn(filmLikes);
        List<Film> films = List.of(Film.builder().id(1L).build(),
                Film.builder().id(2L).build(),
                Film.builder().id(3L).build());
        when(filmDao.getFilms()).thenReturn(films);
        List<User> users = List.of(User.builder().id(1L).build(),
                User.builder().id(2L).build());
        when(userDao.getUsers()).thenReturn(users);
        when(userDao.getUserById(Mockito.anyLong())).thenReturn(Optional.of(User.builder().id(1L).build()));
        List<Film> recommendedFilms = userService.getRecommendedFilms(1L);
        assertNotNull(recommendedFilms);
        assertEquals(1, recommendedFilms.size());
        assertEquals(3,recommendedFilms.get(0).getId());
        when(userDao.getUserById(Mockito.anyLong())).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> userService.getRecommendedFilms(1L));
    }
}
