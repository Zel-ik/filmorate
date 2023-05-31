package ru.yandex.practicum.filmorate.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import ru.yandex.practicum.filmorate.dao.EventDao;
import ru.yandex.practicum.filmorate.dao.FilmDao;
import ru.yandex.practicum.filmorate.dao.ReviewDao;
import ru.yandex.practicum.filmorate.dao.UserDao;
import ru.yandex.practicum.filmorate.entity.Event;
import ru.yandex.practicum.filmorate.entity.Review;
import ru.yandex.practicum.filmorate.entity.film.Film;
import ru.yandex.practicum.filmorate.entity.user.User;
import ru.yandex.practicum.filmorate.exception.ReviewNotFoundException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ReviewServiceTest {

    @InjectMocks
    private ReviewService reviewService;

    @Mock
    private ReviewDao reviewDao;

    @Mock
    private FilmDao filmDao;

    @Mock
    private UserDao userDao;

    @Mock
    private EventDao eventDao;

    @Test
    public void getReviewById() {
        Optional<Review> optionalReview = Optional.of(Review.builder().reviewId(1L)
                .filmId(2L).userId(3L).content("content").build());
        when(reviewDao.getReviewById(Mockito.anyLong())).thenReturn(optionalReview);
        Review review = reviewService.getReviewById(1L);
        assertNotNull(review);
        assertEquals(1, review.getReviewId());
        assertEquals(2, review.getFilmId());
        assertEquals(3, review.getUserId());
        when(reviewDao.getReviewById(Mockito.anyLong())).thenThrow(ReviewNotFoundException.class);
        assertThrows(ReviewNotFoundException.class,() ->  reviewService.getReviewById(1L));
    }

    @Test
    public void addReview() {
        Optional<Review> optionalReview = Optional.of(Review.builder().reviewId(1L)
                .filmId(2L).userId(3L).content("content").build());
        when(reviewDao.getReviewById(Mockito.anyLong())).thenReturn(optionalReview);
        when(filmDao.getFilmById(Mockito.anyLong())).thenReturn(Optional.of(Film.builder().build()));
        when(userDao.getUserById(Mockito.anyLong())).thenReturn(Optional.of(User.builder().build()));
        doNothing().when(eventDao).addEvent(Mockito.any(Event.class));
        Review review = reviewService.addReview(optionalReview.get());
        assertNotNull(review);
        assertEquals(1, review.getReviewId());
        assertEquals(2, review.getFilmId());
        assertEquals(3, review.getUserId());
        when(userDao.getUserById(Mockito.anyLong())).thenThrow(UserNotFoundException.class);
        assertThrows(UserNotFoundException.class,() ->  reviewService.addReview(optionalReview.get()));
    }

    @Test
    public void updateReview() {
        Optional<Review> optionalReview = Optional.of(Review.builder().reviewId(1L)
                .filmId(2L).userId(3L).content("content").build());
        when(reviewDao.getReviewById(Mockito.anyLong())).thenReturn(optionalReview);
        when(filmDao.getFilmById(Mockito.anyLong())).thenReturn(Optional.of(Film.builder().build()));
        when(userDao.getUserById(Mockito.anyLong())).thenReturn(Optional.of(User.builder().build()));
        doNothing().when(eventDao).addEvent(Mockito.any(Event.class));
        Review review = reviewService.updateReview(optionalReview.get());
        assertNotNull(review);
        assertEquals(1, review.getReviewId());
        assertEquals(2, review.getFilmId());
        assertEquals(3, review.getUserId());
        when(userDao.getUserById(Mockito.anyLong())).thenThrow(UserNotFoundException.class);
        assertThrows(UserNotFoundException.class,() ->  reviewService.updateReview(optionalReview.get()));
    }

    @Test
    public void getReviewsByFilmId() {
        List<Review> reviews = List.of(Review.builder().reviewId(1L)
                .filmId(2L).userId(3L).content("content").build());
        when(reviewDao.getReviewsByFilmId(1L, 10)).thenReturn(reviews);
        when(reviewDao.getReviews(10)).thenReturn(reviews);
        when(filmDao.getFilmById(Mockito.anyLong())).thenReturn(Optional.of(Film.builder().build()));
        List<Review> reviewsFilmId = reviewService.getByReviewsFilmId(1L, 10);
        assertNotNull(reviewsFilmId);
        assertEquals(1, reviewsFilmId.size());
        assertEquals(2, reviewsFilmId.get(0).getFilmId());
        assertEquals(3, reviewsFilmId.get(0).getUserId());
        List<Review> reviewCount = reviewService.getByReviewsFilmId(null, 10);
        assertNotNull(reviewCount);
        assertEquals(1, reviewCount.size());
        assertEquals(2, reviewCount.get(0).getFilmId());
        assertEquals(3, reviewCount.get(0).getUserId());
    }
}
