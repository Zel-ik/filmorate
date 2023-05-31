package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.entity.user.User;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface UserDao {

    List<User> getUsers();

    Long addUser(User user);

    void updateUser(User user);

    Optional<User> getUserById(long id);

    void addFriend(long userId, long friendId);

    void deleteFriend(long userId, long friendId);

    Set<User> getUserFriends(long id);

    void deleteUserById(long userId);
}
