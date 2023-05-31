package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.entity.Event;
import ru.yandex.practicum.filmorate.entity.film.Film;
import ru.yandex.practicum.filmorate.entity.user.User;
import ru.yandex.practicum.filmorate.service.EventService;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final EventService eventService;

    @GetMapping(value = "/users/{id}")
    public User getUser(@PathVariable Long id) {
        User user = userService.getUser(id);
        log.trace("Пользователь получен: " + userService.getUser(id));
        return user;
    }

    @GetMapping(value = "/users")
    public List<User> getUsers() {
        List<User> users = userService.getUsers();
        log.trace("Кол-во пользователей: " + userService.getUsers().size());
        return users;
    }

    @GetMapping(value = "/users/{id}/friends")
    public List<User> getUserFriends(@PathVariable Long id) {
        List<User> friendsList = userService.getUserFriends(id);
        log.trace("Кол-во друзей пользователя: " + userService.getUser(id) + " = "
                + friendsList.size());
        return friendsList;
    }

    @GetMapping(value = "/users/{id}/friends/common/{otherId}")
    public List<User> getCommonFriends(
            @PathVariable Long id,
            @PathVariable Long otherId) {
        List<User> commonFriends = userService.getCommonFriends(id, otherId);
        log.trace("Пользователь: " + userService.getUser(id) + ", и пользователь:" + userService.getUser(otherId)
                + " имеют общих друзей: " + commonFriends.size());
        return commonFriends;
    }

    @PostMapping(value = "/users")
    public User addUser(@Valid @RequestBody User user) {
        User addedUser = userService.addUser(user);
        log.trace("Пользователь добавлен: " + addedUser);
        return addedUser;
    }

    @PutMapping(value = "/users")
    public User updateUser(@Valid @RequestBody User user) {
        User updatedUser = userService.updateUser(user);
        log.trace("Пользователь обновлен: " + updatedUser);
        return updatedUser;
    }

    @PutMapping(value = "/users/{id}/friends/{friendId}")
    public User addFriend(
            @PathVariable Long id,
            @PathVariable Long friendId) {
        log.trace("Пользователь (" + userService.getUser(id) + ") добавил в друзья пользователя: "
                + userService.getUser(friendId));
        return userService.addFriend(id, friendId);
    }

    @DeleteMapping(value = "/users/{id}/friends/{friendId}")
    public User deleteFriend(
            @PathVariable Long id,
            @PathVariable Long friendId) {
        log.trace("Пользователь (" + userService.getUser(id) + ") удалил из друзей пользователя: "
                + userService.getUser(friendId));
        return userService.deleteFriend(id, friendId);
    }

    @GetMapping(value = "/users/{id}/recommendations")
    public List<Film> getRecommendedFilms(@PathVariable Long id) {
        List<Film> recommendedFilms = userService.getRecommendedFilms(id);
        log.trace("Получили список рекомендованных фильмов, длина = {}", recommendedFilms.size());
        return recommendedFilms;
    }

    @DeleteMapping(value = "/users/{userId}")
    public void deleteFilmById(@PathVariable Long userId) {
        userService.deleteUserById(userId);
        log.trace("Пользователь id={} удален", userId);
    }

    @GetMapping(value = "/users/{id}/feed")
    public List<Event> getUserFeed(@PathVariable("id") Long userId) {
        log.trace("Запрошена лента событий пользователя c id=" + userId);
        return eventService.getUserFeed(userId);
    }
}
