package ru.yandex.practicum.filmorate.entity.film;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;

public class FilmReleaseDateValidator implements ConstraintValidator<FilmReleaseDate, LocalDate> {

    private static final LocalDate MIN_FILM_RELEASE_DATE = LocalDate.of(1895, 12, 28);

    @Override
    public boolean isValid(LocalDate filmReleaseDate, ConstraintValidatorContext context) {
        return filmReleaseDate.isAfter(MIN_FILM_RELEASE_DATE);
    }
}
