package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.entity.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;

import javax.validation.Valid;
import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping(value = "/reviews")
    public Review addReview(@Valid @RequestBody Review review) {
        log.trace("Отзыв " + review + " был добавлен");
        return reviewService.addReview(review);
    }


    @GetMapping(value = "/reviews/{id}")
    public Review getReview(@PathVariable Long id) {
        Review review = reviewService.getReviewById(id);
        log.trace("Отзыв получен: " + review);
        return review;
    }

    @PutMapping(value = "/reviews")
    public Review updateReview(@Valid @RequestBody Review review) {
        Review updatedReview = reviewService.updateReview(review);
        log.trace("Отзыв " + review + " был обновлен");
        return updatedReview;
    }

    @DeleteMapping(value = "/reviews/{id}")
    public void deleteReview(@PathVariable Long id) {
        log.trace("Происходи удаление отзыва с id =" + id);
        reviewService.deleteReview(id);

    }

    @GetMapping(value = "/reviews")
    @ResponseBody
    public List<Review> getReviewsByFilmId(
            @RequestParam(name = "filmId", required = false) Long filmId,
            @RequestParam(name = "count", required = false, defaultValue = "10") Integer count) {
        List<Review> reviews = reviewService.getByReviewsFilmId(filmId, count);
        log.trace(" Получен список отзывов по id фильма {}\n" + reviews, filmId);
        return reviews;
    }

    @PutMapping("/reviews/{id}/like/{userId}")
    public void addLike(@PathVariable(name = "id") Long reviewId,
                        @PathVariable(name = "userId") Long userId) {
        log.trace("добавлен лайк отзыву id = {}", reviewId);
        reviewService.addLike(reviewId, userId);
    }

    @DeleteMapping("/reviews/{id}/like/{userId}")
    public void deleteLike(@PathVariable(name = "id") Long reviewId,
                           @PathVariable(name = "userId") Long userId) {
        log.trace("удален лайк отзыву id = {}", reviewId);
        reviewService.deleteLike(reviewId, userId);
    }

    @PutMapping("/reviews/{id}/dislike/{userId}")
    public void addDislike(@PathVariable(name = "id") Long reviewId,
                           @PathVariable(name = "userId") Long userId) {
        log.trace("добавлен дизлайк отзыва id = {}", reviewId);
        reviewService.addDislike(reviewId, userId);
    }

    @DeleteMapping("/reviews/{id}/dislike/{userId}")
    public void deleteDislike(@PathVariable(name = "id") Long reviewId,
                              @PathVariable(name = "userId") Long userId) {
        log.trace("удален дизлайк отзыва id = {}", reviewId);
        reviewService.deleteDislike(reviewId, userId);
    }
}
