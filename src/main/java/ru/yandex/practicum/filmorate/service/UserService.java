package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.EventDao;
import ru.yandex.practicum.filmorate.dao.FilmDao;
import ru.yandex.practicum.filmorate.dao.UserDao;
import ru.yandex.practicum.filmorate.entity.Event;
import ru.yandex.practicum.filmorate.entity.EventType;
import ru.yandex.practicum.filmorate.entity.Operation;
import ru.yandex.practicum.filmorate.entity.film.Film;
import ru.yandex.practicum.filmorate.entity.film.FilmLikes;
import ru.yandex.practicum.filmorate.entity.user.User;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final UserDao userDao;
    private final EventDao eventDao;
    private final FilmDao filmDao;

    public List<User> getUsers() {
        return userDao.getUsers();
    }

    public User addUser(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        Long id = userDao.addUser(user);
        return userDao.getUserById(id).orElseThrow(() -> new UserNotFoundException("Пользователь не добавился."));
    }

    public User updateUser(User user) {
        if (userDao.getUserById(user.getId()).isEmpty()) {
            throw new UserNotFoundException("Пользователя с id = " + user.getId() + " не существует.");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        userDao.updateUser(user);
        return userDao.getUserById(user.getId())
                .orElseThrow(() -> new UserNotFoundException("Пользователь не обновился."));
    }

    public User getUser(Long id) {
        return userDao.getUserById(id)
                .orElseThrow(() -> new UserNotFoundException("Пользователя с id = " + id + " не существует."));
    }

    public User addFriend(Long id, Long friendId) {
        if (userDao.getUserById(id).isEmpty()) {
            throw new UserNotFoundException("Пользователя с id = " + id + " не существует.");
        }
        if (userDao.getUserById(friendId).isEmpty()) {
            throw new UserNotFoundException("Пользователя с id = " + friendId + " не существует.");
        }
        userDao.addFriend(id, friendId);
        eventDao.addEvent(Event.builder()
                .userId(id)
                .eventType(EventType.FRIEND)
                .operation(Operation.ADD)
                .timestamp(Instant.now().toEpochMilli())
                .entityId(friendId)
                .build());
        log.trace("Создано событие: Пользователь с id =" + id + " добавил в друзья пользователя с id=" + friendId);
        return userDao.getUserById(id).get();
    }

    public User deleteFriend(Long id, Long friendId) {
        if (userDao.getUserById(id).isEmpty()) {
            throw new UserNotFoundException("Пользователя с id = " + id + " не существует.");
        }
        if (userDao.getUserById(friendId).isEmpty()) {
            throw new UserNotFoundException("Пользователя с id = " + friendId + " не существует.");
        }
        userDao.deleteFriend(id, friendId);
        eventDao.addEvent(Event.builder()
                .userId(id)
                .eventType(EventType.FRIEND)
                .operation(Operation.REMOVE)
                .timestamp(Instant.now().toEpochMilli())
                .entityId(friendId)
                .build());
        log.trace("Создано событие: Пользователь с id =" + id + " удалил из друзей пользователя с id=" + friendId);
        return userDao.getUserById(id).get();
    }

    public List<User> getUserFriends(Long id) {
        if (userDao.getUserById(id).isEmpty()) {
            throw new UserNotFoundException("Пользователя с id = " + id + " не существует.");
        }
        return new ArrayList<>(userDao.getUserFriends(id));
    }

    public List<User> getCommonFriends(Long id, Long otherId) {
        if (userDao.getUserById(id).isEmpty()) {
            throw new UserNotFoundException("Пользователя с id = " + id + " не существует.");
        }
        if (userDao.getUserById(otherId).isEmpty()) {
            throw new UserNotFoundException("Пользователя с id = " + otherId + " не существует.");
        }
        ArrayList<User> userFriends = new ArrayList<>(this.getUserFriends(id));
        ArrayList<User> otherUserFriends = new ArrayList<>(this.getUserFriends(otherId));
        return userFriends.stream().filter(otherUserFriends::contains).collect(Collectors.toList());
    }

    public List<Film> getRecommendedFilms(Long userId) {
        Optional<User> optionalUser = userDao.getUserById(userId);
        if (optionalUser.isEmpty()) {
            throw new UserNotFoundException("Пользователя с id = " + userId + " не существует.");
        }
        List<Film> films = filmDao.getFilms();
        List<User> users = userDao.getUsers();
        List<FilmLikes> filmLikes = filmDao.findAllFilmLikes();
        Map<User, HashMap<Film, Integer>> data = new HashMap<>();
        for (User user : users) {
            HashMap<Film, Integer> userLikes = new HashMap<>();
            films.forEach(film -> userLikes.put(film, getRating(filmLikes, user, film)));
            data.put(user, userLikes);
        }
        return getRecommendedFilms(data, optionalUser.get());
    }

    private Integer getRating(List<FilmLikes> filmLikes, User user, Film film) {
        if (filmLikes.stream()
                .anyMatch(filmLikes1 -> (filmLikes1.getFilmId() == film.getId()
                        && filmLikes1.getUserId() == user.getId())))
            return 1;
        return 0;
    }

    private List<Film> getRecommendedFilms(Map<User, HashMap<Film, Integer>> data, User userInput) {
        Map<Film, Integer> userInputData = data.get(userInput);
        User maxMatchUser = User.builder().build();
        int maxMatches = 0;
        for (User user : data.keySet()) {
            if (user.equals(userInput))
                continue;
            int countMatches = 0;
            Map<Film, Integer> userData = data.get(user);
            for (Film film : userData.keySet()) {
                if (userData.get(film).equals(userInputData.get(film))
                        && userInputData.get(film) == 1
                        && userData.get(film) == 1) {
                    ++countMatches;
                }
            }
            if (maxMatches < countMatches) {
                maxMatches = countMatches;
                maxMatchUser = user;
            }
        }
        if (maxMatches == 0) {
            return Collections.emptyList();
        }
        Map<Film, Integer> maxMatchData = data.get(maxMatchUser);
        return maxMatchData.keySet().stream()
                .filter(film -> (!maxMatchData.get(film).equals(userInputData.get(film))
                        && userInputData.get(film) == 0))
                .collect(Collectors.toList());
    }

    public void deleteUserById(Long userId) {
        if (userDao.getUserById(userId).isEmpty()) {
            throw new UserNotFoundException(String.format("Пользователя с id = '%s' не существует.", userId));
        }
        userDao.deleteUserById(userId);
    }
}
