package ru.yandex.practicum.filmorate.entity.film;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Director {

    private Long id;

    @NotBlank
    private String name;
}
