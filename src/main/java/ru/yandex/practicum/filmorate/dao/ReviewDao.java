package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.entity.Review;

import java.util.List;
import java.util.Optional;

public interface ReviewDao {

    Long addReview(Review review);

    Optional<Review> getReviewById(long id);

    List<Review> getReviews(int count);

    void updateReview(Review review);

    void deleteReview(long id);

    List<Review> getReviewsByFilmId(long filmId, int count);

    void addLike(long reviewId, long userId);

    void addDislike(long reviewId, long userId);

    void deleteLike(long reviewId, long userId);

    void deleteDislike(long reviewId, long userId);
}
