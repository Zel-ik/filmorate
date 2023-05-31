package ru.yandex.practicum.filmorate.entity.film;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Film {

    private long id;

    @NotBlank
    private String name;

    @Size(max = 200)
    private String description;

    @FilmReleaseDate
    private LocalDate releaseDate;

    @Positive
    private int duration;

    private Integer rate;

    private Mpa mpa;

    private Set<Genre> genres;

    private Set<Director> directors;
}
