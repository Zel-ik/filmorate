package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.entity.Event;
import ru.yandex.practicum.filmorate.entity.EventType;
import ru.yandex.practicum.filmorate.entity.Operation;
import ru.yandex.practicum.filmorate.entity.user.User;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class EventDaoTest {

    private final EventDao eventDao;

    private final UserDao userDao;

    @BeforeEach
    public void beforeEach() {
        userDao.addUser(User.builder().name("name1").login("login1")
                .email("email1@mail.com").birthday(LocalDate.of(2000,4,5)).build());
        userDao.addUser(User.builder().name("name2").login("login2")
                .email("email2@mail.com").birthday(LocalDate.of(1999,2,3)).build());
        eventDao.addEvent(Event.builder()
                .userId(1L)
                .eventType(EventType.FRIEND)
                .operation(Operation.ADD)
                .timestamp(Instant.now().toEpochMilli())
                .entityId(2L)
                .build());
    }

    @Test
    @DirtiesContext
    public void getUserFeed() {
        List<Event> events = eventDao.getUserFeed(1L);
        assertNotNull(events);
        assertEquals(1, events.size());
        assertEquals(1, events.get(0).getEventId());
    }

    @Test
    @DirtiesContext
    public void addUserEvent() {
        eventDao.addEvent(Event.builder()
                .userId(1L)
                .eventType(EventType.FRIEND)
                .operation(Operation.REMOVE)
                .timestamp(Instant.now().toEpochMilli())
                .entityId(2L)
                .build());
        List<Event> events = eventDao.getUserFeed(1L);
        assertNotNull(events);
        assertEquals(2, events.size());
        assertEquals(1, events.get(0).getEventId());
    }
}
