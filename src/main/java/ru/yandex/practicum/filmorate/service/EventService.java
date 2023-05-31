package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.EventDao;
import ru.yandex.practicum.filmorate.dao.UserDao;
import ru.yandex.practicum.filmorate.entity.Event;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class EventService {
    private final UserDao userDao;
    private final EventDao eventDao;

    public List<Event> getUserFeed(Long userId) {
        if (userDao.getUserById(userId).isEmpty()) {
            throw new UserNotFoundException("Невозможно получить ленту событий, пользователя с id="
                    + userId + " не существует.");
        }
        return eventDao.getUserFeed(userId);
    }
}
