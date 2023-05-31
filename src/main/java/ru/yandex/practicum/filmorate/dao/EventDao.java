package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.entity.Event;

import java.util.List;

public interface EventDao {
    List<Event> getUserFeed(long userId);

    void addEvent(Event event);
}
