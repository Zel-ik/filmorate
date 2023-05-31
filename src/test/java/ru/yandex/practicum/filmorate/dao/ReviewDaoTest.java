package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.entity.Review;
import ru.yandex.practicum.filmorate.entity.film.Film;
import ru.yandex.practicum.filmorate.entity.user.User;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ReviewDaoTest {

    private final FilmDao filmDao;

    private final UserDao userDao;

    private final ReviewDao reviewDao;

    @BeforeEach
    public void beforeEach() {
        userDao.addUser(User.builder().name("user1").login("login1")
                .email("email1@mail.com").birthday(LocalDate.of(2000, 4, 5)).build());
        userDao.addUser(User.builder().name("user2").login("login2")
                .email("email2@mail.com").birthday(LocalDate.of(2001, 4, 5)).build());
        filmDao.addFilm(Film.builder().name("name1").description("desc1").duration(120)
                .releaseDate(LocalDate.of(2022, 10, 5)).build());
        filmDao.addFilm(Film.builder().name("name2").description("desc2").duration(110)
                .releaseDate(LocalDate.of(2021, 10, 5)).build());
        reviewDao.addReview(Review.builder().userId(1L).filmId(2L).isPositive(false).content("content1").build());
        reviewDao.addReview(Review.builder().userId(2L).filmId(1L).isPositive(true).content("content2").build());
    }

    @Test
    @DirtiesContext
    public void getReviews() {
        List<Review> reviews = reviewDao.getReviews(10);
        assertNotNull(reviews);
        assertEquals(2, reviews.size());
        assertEquals(1, reviews.get(0).getReviewId());
    }

    @Test
    @DirtiesContext
    public void getReviewById() {
        Optional<Review> review = reviewDao.getReviewById(2L);
        assertNotNull(review);
        assertTrue(review.isPresent());
        assertEquals(2, review.get().getReviewId());
        assertEquals(1, review.get().getFilmId());
        assertEquals(2, review.get().getUserId());
    }

    @Test
    @DirtiesContext
    public void getReviewsByFilmId() {
        List<Review> reviews = reviewDao.getReviewsByFilmId(1L, 10);
        assertNotNull(reviews);
        assertEquals(1, reviews.size());
        assertEquals(2, reviews.get(0).getReviewId());
    }

    @Test
    @DirtiesContext
    public void addReview() {
        Long id = reviewDao.addReview(Review.builder().userId(1L).filmId(1L)
                .isPositive(false).content("content3").build());
        assertNotNull(id);
        List<Review> reviews = reviewDao.getReviewsByFilmId(1L, 10);
        assertNotNull(reviews);
        assertEquals(2, reviews.size());
        assertEquals(3, reviews.get(1).getReviewId());
    }

    @Test
    @DirtiesContext
    public void updateReview() {
        reviewDao.updateReview(Review.builder().reviewId(1L).userId(2L).filmId(1L)
                .isPositive(false).content("updated").build());
        Optional<Review> review = reviewDao.getReviewById(1L);
        assertNotNull(review);
        assertTrue(review.isPresent());
        assertEquals(1, review.get().getReviewId());
        assertEquals("updated", review.get().getContent());
    }

    @Test
    @DirtiesContext
    public void deleteReview() {
        reviewDao.deleteReview(1L);
        List<Review> reviews = reviewDao.getReviewsByFilmId(2L, 10);
        assertNotNull(reviews);
        assertTrue(reviews.isEmpty());
    }

    @Test
    @DirtiesContext
    public void addLike() {
        reviewDao.addLike(1L, 2L);
        Optional<Review> review = reviewDao.getReviewById(1L);
        assertNotNull(review);
        assertTrue(review.isPresent());
        assertEquals(1, review.get().getReviewId());
        assertEquals(1, review.get().getUseful());
    }

    @Test
    @DirtiesContext
    public void addDislike() {
        reviewDao.addDislike(1L, 2L);
        Optional<Review> review = reviewDao.getReviewById(1L);
        assertNotNull(review);
        assertTrue(review.isPresent());
        assertEquals(1, review.get().getReviewId());
        assertEquals(-1, review.get().getUseful());
    }

    @Test
    @DirtiesContext
    public void deleteLike() {
        reviewDao.addLike(1L, 2L);
        reviewDao.deleteLike(1L, 2L);
        Optional<Review> review = reviewDao.getReviewById(1L);
        assertNotNull(review);
        assertTrue(review.isPresent());
        assertEquals(1, review.get().getReviewId());
        assertEquals(0, review.get().getUseful());
    }

    @Test
    @DirtiesContext
    public void deleteDislike() {
        reviewDao.addDislike(1L, 2L);
        reviewDao.deleteDislike(1L, 2L);
        Optional<Review> review = reviewDao.getReviewById(1L);
        assertNotNull(review);
        assertTrue(review.isPresent());
        assertEquals(1, review.get().getReviewId());
        assertEquals(0, review.get().getUseful());
    }
}
