package ru.yandex.practicum.filmorate.entity.film;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FilmLikes {

    private long filmId;

    private long userId;
}
