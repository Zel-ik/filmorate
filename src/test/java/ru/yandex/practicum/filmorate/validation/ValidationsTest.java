package ru.yandex.practicum.filmorate.validation;

import org.junit.Before;
import org.junit.Test;
import ru.yandex.practicum.filmorate.entity.Review;
import ru.yandex.practicum.filmorate.entity.film.Film;
import ru.yandex.practicum.filmorate.entity.user.User;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;

public class ValidationsTest {

    private Validator validator;

    @Before
    public void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void testUserInvalidEmail() {
        User user1 = User.builder().id(1).email("invalidEmail").login("login").name("name").build();
        Set<ConstraintViolation<User>> violations = validator.validate(user1);
        assertFalse(violations.isEmpty());
    }

    @Test
    public void testUserInvalidLogin() {
        User user1 = User.builder().id(1).email("name@mail.com").login("").name("name").build();
        Set<ConstraintViolation<User>> violations = validator.validate(user1);
        assertFalse(violations.isEmpty());
        User user2 = User.builder().id(1).email("name@mail.com").login("log in").name("name").build();
        Set<ConstraintViolation<User>> violations2 = validator.validate(user2);
        assertFalse(violations2.isEmpty());
    }

    @Test
    public void testUserInvalidBirthday() {
        User user1 = User.builder().id(1).email("name@mail.com").login("").name("name")
                .birthday(LocalDate.now().plusDays(1)).build();
        Set<ConstraintViolation<User>> violations = validator.validate(user1);
        assertFalse(violations.isEmpty());
    }

    @Test
    public void testFilmInvalidName() {
        Film film = Film.builder()
                .id(1)
                .name("")
                .description("description")
                .releaseDate(LocalDate.now())
                .duration(100).build();
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty());
    }

    @Test
    public void testFilmInvalidDescription() {
        StringBuilder str = new StringBuilder();
        str.setLength(201);
        Film film = Film.builder()
                .id(1)
                .name("name")
                .description(str.toString())
                .releaseDate(LocalDate.now())
                .duration(100).build();
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty());
    }

    @Test
    public void testFilmInvalidReleaseDate() {
        Film film = Film.builder()
                .id(1)
                .name("name")
                .description("description")
                .releaseDate(LocalDate.of(1895, 12, 27))
                .duration(100).build();
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty());
    }

    @Test
    public void testFilmInvalidDuration() {
        Film film = Film.builder()
                .id(1)
                .name("name")
                .description("description")
                .releaseDate(LocalDate.now())
                .duration(-1).build();
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty());
    }

    @Test
    public void testReviewInvalidUserId() {
        Review review = Review.builder()
                .reviewId(1L)
                .filmId(2L)
                .content("content")
                .build();
        Set<ConstraintViolation<Review>> violations = validator.validate(review);
        assertFalse(violations.isEmpty());
    }

    @Test
    public void testReviewInvalidFilmId() {
        Review review = Review.builder()
                .reviewId(1L)
                .userId(2L)
                .content("content")
                .build();
        Set<ConstraintViolation<Review>> violations = validator.validate(review);
        assertFalse(violations.isEmpty());
    }
}
