package ru.yandex.practicum.filmorate.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Event {
    private Long eventId;
    private Long userId;
    private EventType eventType;
    private Operation operation;
    private Long timestamp;
    private Long entityId;
}
