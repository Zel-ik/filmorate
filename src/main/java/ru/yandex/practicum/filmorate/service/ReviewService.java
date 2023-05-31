package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.EventDao;
import ru.yandex.practicum.filmorate.dao.FilmDao;
import ru.yandex.practicum.filmorate.dao.ReviewDao;
import ru.yandex.practicum.filmorate.dao.UserDao;
import ru.yandex.practicum.filmorate.entity.Event;
import ru.yandex.practicum.filmorate.entity.EventType;
import ru.yandex.practicum.filmorate.entity.Operation;
import ru.yandex.practicum.filmorate.entity.Review;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.ReviewNotFoundException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;

import java.time.Instant;
import java.util.List;


@Service
@Slf4j
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewDao reviewDao;
    private final FilmDao filmDao;
    private final UserDao userDao;
    private final EventDao eventDao;

    public Review getReviewById(Long id) {
        return reviewDao.getReviewById(id)
                .orElseThrow(() -> new ReviewNotFoundException("Отзыва с id = " + id + " не существует."));
    }

    public Review addReview(Review review) {
        if (filmDao.getFilmById(review.getFilmId()).isEmpty()) {
            throw new FilmNotFoundException(String.format("Фильм с id = '%s' не найден.", review.getFilmId()));
        }
        if (userDao.getUserById(review.getUserId()).isEmpty()) {
            throw new UserNotFoundException(String.format("Пользователь с id = '%s' не найден.", review.getUserId()));
        }
        Long id = reviewDao.addReview(review);
        log.info("ПОЛУЧЕН ID = {}", id);
        eventDao.addEvent(Event.builder()
                .userId(review.getUserId())
                .eventType(EventType.REVIEW)
                .operation(Operation.ADD)
                .timestamp(Instant.now().toEpochMilli())
                .entityId(id)
                .build());
        log.trace("Создано событие: Пользователь: с id = "
                + review.getUserId() + " добавил отзыв фильму с id = " + review.getFilmId());
        return reviewDao.getReviewById(id)
                .orElseThrow(() -> new ReviewNotFoundException("Отзыв не добавился"));
    }

    public Review updateReview(Review review) {
        filmDao.getFilmById(review.getFilmId()).orElseThrow(() -> new FilmNotFoundException("film не существует"));
        userDao.getUserById(review.getUserId()).orElseThrow(() -> new UserNotFoundException("user не существует"));
        reviewDao.updateReview(review);
        eventDao.addEvent(Event.builder()
                .userId(getReviewById(review.getReviewId()).getUserId())
                .eventType(EventType.REVIEW)
                .operation(Operation.UPDATE)
                .timestamp(Instant.now().toEpochMilli())
                .entityId(review.getReviewId())
                .build());
        log.trace("Создано событие: Пользователь: с id = "
                + review.getUserId() + " обновил отзыв фильму с id = " + review.getFilmId());
        return reviewDao.getReviewById(review.getReviewId())
                .orElseThrow(() -> new ReviewNotFoundException("Отзыв не обновился"));
    }

    public void deleteReview(Long id) {
        var review = reviewDao.getReviewById(id)
                .orElseThrow(() -> new ReviewNotFoundException("Отзыв с данным id не был найден. Id : " + id));
        reviewDao.deleteReview(review.getReviewId());
        eventDao.addEvent(Event.builder()
                .userId(review.getUserId())
                .eventType(EventType.REVIEW)
                .operation(Operation.REMOVE)
                .timestamp(Instant.now().toEpochMilli())
                .entityId(id)
                .build());
        log.trace("Создано событие: Пользователь: с id = " + review.getUserId() + " удалил отзыв фильму с id = " + review.getFilmId());
    }

    public List<Review> getByReviewsFilmId(Long filmId, Integer count) {
        if (filmId == null) {
            return reviewDao.getReviews(count);
        }
        if (filmDao.getFilmById(filmId).isEmpty()) {
            throw new FilmNotFoundException(String.format("Фильм с id = '%s' не найден.", filmId));
        }
        return reviewDao.getReviewsByFilmId(filmId, count);
    }

    public void addLike(Long reviewId, Long userId) {
        reviewDao.getReviewById(reviewId)
                .orElseThrow(() -> new ReviewNotFoundException("Отзыв с данным id не был найден. Id : " + reviewId));
        userDao.getUserById(userId).orElseThrow(() -> new UserNotFoundException("user not found"));
        reviewDao.addLike(reviewId, userId);
    }

    public void addDislike(Long reviewId, Long userId) {
        reviewDao.getReviewById(reviewId)
                .orElseThrow(() -> new ReviewNotFoundException("Отзыв с данным id не был найден. Id : " + reviewId));
        userDao.getUserById(userId).orElseThrow(() -> new UserNotFoundException("user not found"));
        reviewDao.addDislike(reviewId, userId);
    }

    public void deleteLike(Long reviewId, Long userId) {
        reviewDao.getReviewById(reviewId)
                .orElseThrow(() -> new ReviewNotFoundException("Отзыв с данным id не был найден. Id : " + reviewId));
        userDao.getUserById(userId).orElseThrow(() -> new UserNotFoundException("user not found"));
        reviewDao.deleteLike(reviewId, userId);
    }

    public void deleteDislike(Long reviewId, Long userId) {
        reviewDao.getReviewById(reviewId)
                .orElseThrow(() -> new ReviewNotFoundException("Отзыв с данным id не был найден. Id : " + reviewId));
        userDao.getUserById(userId).orElseThrow(() -> new UserNotFoundException("user not found"));
        reviewDao.deleteDislike(reviewId, userId);
    }
}
