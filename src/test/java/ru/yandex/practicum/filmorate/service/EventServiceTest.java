package ru.yandex.practicum.filmorate.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import ru.yandex.practicum.filmorate.dao.EventDao;
import ru.yandex.practicum.filmorate.dao.UserDao;
import ru.yandex.practicum.filmorate.entity.Event;
import ru.yandex.practicum.filmorate.entity.user.User;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class EventServiceTest {

    @InjectMocks
    private EventService eventService;

    @Mock
    private EventDao eventDao;

    @Mock
    private UserDao userDao;

    @Test
    public void getUserFeed() {
        List<Event> events = List.of(Event.builder().eventId(1L).userId(2L).build());
        when(eventDao.getUserFeed(Mockito.anyLong())).thenReturn(events);
        when(userDao.getUserById(Mockito.anyLong())).thenReturn(Optional.of(User.builder().build()));
        List<Event> eventList = eventService.getUserFeed(2L);
        assertNotNull(eventList);
        assertEquals(1, eventList.size());
        assertEquals(1L, eventList.get(0).getEventId());
        when(userDao.getUserById(Mockito.anyLong())).thenReturn(Optional.empty());
        assertThrows((UserNotFoundException.class), () -> eventService.getUserFeed(2L));
    }
}
