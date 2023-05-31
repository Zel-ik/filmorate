package ru.yandex.practicum.filmorate.dao.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.EventDao;
import ru.yandex.practicum.filmorate.entity.Event;
import ru.yandex.practicum.filmorate.entity.EventType;
import ru.yandex.practicum.filmorate.entity.Operation;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Objects;

@Component
@Slf4j
@RequiredArgsConstructor
public class EventDaoImpl implements EventDao {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public void addEvent(Event event) {
        String sql = "INSERT INTO \"events\" (USER_ID, EVENT_TYPE, OPERATION, TIMESTAMP, ENTITY_ID) VALUES (?,?,?,?,?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(conn -> {
            PreparedStatement preparedStatement = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setLong(1, event.getUserId());
            preparedStatement.setString(2, event.getEventType().toString());
            preparedStatement.setString(3, event.getOperation().toString());
            preparedStatement.setLong(4, event.getTimestamp());
            preparedStatement.setLong(5, event.getEntityId());
            return preparedStatement;
        }, keyHolder);
        event.setEventId(Objects.requireNonNull(keyHolder.getKey()).longValue());
    }

    @Override
    public List<Event> getUserFeed(long userId) {
        String sql = "SELECT * FROM \"events\" WHERE USER_ID = ? ORDER BY TIMESTAMP";
        List<Event> userEvents = jdbcTemplate.query(sql, (rs, rowNum) -> Event.builder()
                .eventId(rs.getLong("EVENT_ID"))
                .userId(rs.getLong("USER_ID"))
                .eventType(EventType.valueOf(rs.getString("EVENT_TYPE")))
                .operation(Operation.valueOf(rs.getString("OPERATION")))
                .timestamp(rs.getLong("TIMESTAMP"))
                .entityId(rs.getLong("ENTITY_ID"))
                .build(), userId);
        log.info("Лента событий пользователя id=" + userId + " получена, длина = {}", userEvents.size());
        return userEvents;
    }
}
