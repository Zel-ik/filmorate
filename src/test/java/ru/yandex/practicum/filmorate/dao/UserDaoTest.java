package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.entity.user.User;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserDaoTest {

    private final UserDao userDao;

    @BeforeEach
    public void beforeEach() {
        userDao.addUser(User.builder()
                .email("name1@mail.com")
                .login("login1")
                .name("name1")
                .birthday(LocalDate.of(1999, 2, 3))
                .build());
        userDao.addUser(User.builder()
                .email("name2@mail.com")
                .login("login2")
                .name("name2")
                .birthday(LocalDate.of(1997, 3, 7))
                .build());
        userDao.addUser(User.builder()
                .email("name3@mail.com")
                .login("login3")
                .name("name3")
                .birthday(LocalDate.of(1997, 3, 7))
                .build());
    }

    @Test
    @DirtiesContext
    public void getUserById() {
        Optional<User> user = userDao.getUserById(2L);
        assertNotNull(user);
        assertTrue(user.isPresent());
        assertEquals(2, user.get().getId());
        assertEquals("name2@mail.com", user.get().getEmail());
        assertEquals("login2", user.get().getLogin());
        assertEquals("name2", user.get().getName());

        Optional<User> getFakeUser = userDao.getUserById(99L);
        assertNotNull(getFakeUser);
        assertTrue(getFakeUser.isEmpty());
    }

    @Test
    @DirtiesContext
    public void addUser() {
        User user = User.builder()
                .email("name4@mail.com")
                .login("login4")
                .name("name4")
                .birthday(LocalDate.of(2000, 6, 27))
                .build();
        Long id = userDao.addUser(user);
        assertNotNull(id);
        Optional<User> addedUser = userDao.getUserById(id);
        assertNotNull(user);
        assertTrue(addedUser.isPresent());
        assertEquals(4, addedUser.get().getId());
    }

    @Test
    @DirtiesContext
    public void getAllUser() {
        List<User> users = userDao.getUsers();
        assertNotNull(users);
        assertEquals(3, users.size());
    }

    @Test
    @DirtiesContext
    public void updateUser() {
        User user = User.builder()
                .id(1L)
                .email("new@mail.com")
                .login("newLogin")
                .name("newName")
                .birthday(LocalDate.of(1999, 2, 3))
                .build();
        userDao.updateUser(user);
        Optional<User> updatedUser = userDao.getUserById(user.getId());
        assertNotNull(updatedUser);
        assertTrue(updatedUser.isPresent());
        assertEquals(1, updatedUser.get().getId());
        assertEquals("new@mail.com", updatedUser.get().getEmail());
        assertEquals("newLogin", updatedUser.get().getLogin());
        assertEquals("newName", updatedUser.get().getName());
        assertEquals(LocalDate.of(1999, 2, 3), updatedUser.get().getBirthday());
    }

    @Test
    @DirtiesContext
    public void getUserFriends() {
        Set<User> users = userDao.getUserFriends(1L);
        assertNotNull(users);
        assertEquals(0, users.size());
    }

    @Test
    @DirtiesContext
    public void addFriend() {
        userDao.addFriend(1L, 2L);
        userDao.addFriend(1L, 3L);
        Set<User> friendsUser1 = userDao.getUserFriends(1L);
        assertNotNull(friendsUser1);
        assertEquals(2, friendsUser1.size());
    }

    @Test
    @DirtiesContext
    public void deleteFriend() {
        userDao.addFriend(1L, 2L);
        userDao.addFriend(1L, 3L);
        Set<User> friendsUser1 = userDao.getUserFriends(1L);
        assertNotNull(friendsUser1);
        assertEquals(2, friendsUser1.size());
        userDao.deleteFriend(1L, 2L);
        friendsUser1 = userDao.getUserFriends(1L);
        assertNotNull(friendsUser1);
        assertEquals(1, friendsUser1.size());
    }

    @Test
    @DirtiesContext
    public void deleteUserById() {
        userDao.deleteUserById(2L);
        List<User> users = userDao.getUsers();
        assertNotNull(users);
        assertEquals(2, users.size());
    }
}
