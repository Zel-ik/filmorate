package ru.yandex.practicum.filmorate.dao.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.ReviewDao;
import ru.yandex.practicum.filmorate.entity.Review;
import ru.yandex.practicum.filmorate.exception.IncorrectParameterException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Component
@Slf4j
@RequiredArgsConstructor
public class ReviewDaoImpl implements ReviewDao {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Optional<Review> getReviewById(long id) {
        SqlRowSet reviewRow = jdbcTemplate.queryForRowSet("SELECT * FROM \"reviews\" f WHERE REVIEW_ID = ?;", id);
        if (reviewRow.next()) {
            Review review = Review.builder()
                    .reviewId(reviewRow.getLong("REVIEW_ID"))
                    .content(Objects.requireNonNull(reviewRow.getString("CONTENT")).strip())
                    .isPositive(reviewRow.getBoolean("IS_POSITIVE"))
                    .userId(reviewRow.getLong("USER_ID"))
                    .filmId(reviewRow.getLong("FILM_ID"))
                    .useful(reviewRow.getInt("USEFUL"))
                    .build();
            log.info("Найден отзыв c id = {} ", review.getReviewId());
            return Optional.of(review);
        } else {
            log.info("Отзыв с id = {} не найден.", id);
            return Optional.empty();
        }
    }

    @Override
    public List<Review> getReviews(int count) {
        String sql = "SELECT * FROM \"reviews\" ORDER BY USEFUL DESC LIMIT ?;";
        List<Review> reviews = jdbcTemplate.query(sql, this::mapRowToReview, count);
        log.info("Список всех отзывов получен. Длина = {}", reviews.size());
        return reviews;
    }

    @Override
    public Long addReview(Review review) {
        String sqlReview = "INSERT INTO \"reviews\" (CONTENT, IS_POSITIVE, USER_ID, FILM_ID) VALUES" +
                " (?,?,?,?);";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(conn -> {
            PreparedStatement preparedStatement = conn.prepareStatement(sqlReview, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, review.getContent().strip());
            if (review.getIsPositive() == null) {
                throw new IncorrectParameterException("isPositive is null");
            }
            preparedStatement.setBoolean(2, review.getIsPositive());
            preparedStatement.setLong(3, review.getUserId());
            preparedStatement.setLong(4, review.getFilmId());
            return preparedStatement;
        }, keyHolder);
        Long id = Objects.requireNonNull(keyHolder.getKey()).longValue();
        log.info("Отзыв с id={} добавлен", id);
        return id;
    }

    @Override
    public void updateReview(Review review) {
        String sql = "UPDATE \"reviews\" SET CONTENT = ?, IS_POSITIVE = ?  WHERE REVIEW_ID=  ?";

        jdbcTemplate.update(sql,
                review.getContent(),
                review.getIsPositive(),
                review.getReviewId());
        log.info("Отзыв с id = {} обновлен", review.getReviewId());
    }

    @Override
    public void deleteReview(long id) {
        String sql = "DELETE FROM \"reviews\" WHERE REVIEW_ID = ?";
        jdbcTemplate.update(sql, id);
    }

    public List<Review> getReviewsByFilmId(long filmId, int count) {
        String sql = "SELECT * FROM \"reviews\" WHERE FILM_ID = ? ORDER BY USEFUL DESC LIMIT ?";
        List<Review> reviews = jdbcTemplate.query(sql, this::mapRowToReview, filmId, count);
        log.info(" популярных отзывов получен. Длина = {} , count = {}", reviews.size(), count);
        return reviews;

    }

    @Override
    public void addLike(long reviewId, long userId) {
        String sql = "INSERT INTO \"review_likes\" (REVIEW_ID,USER_ID,IS_LIKED) VALUES(?,?,true)";
        jdbcTemplate.update(sql, reviewId, userId);
        String sqlReviews = "UPDATE \"reviews\" SET USEFUL = USEFUL + 1 WHERE REVIEW_ID = ?;";
        jdbcTemplate.update(sqlReviews, reviewId);
        log.info("Отзыву id={} поставил лайк пользователь id={}", reviewId, userId);
        log.info("измененный отзыв" + getReviewById(reviewId));
    }

    @Override
    public void addDislike(long reviewId, long userId) {
        String sql = "INSERT INTO \"review_likes\" (REVIEW_ID,USER_ID,IS_LIKED) VALUES(?,?,false)";
        jdbcTemplate.update(sql, reviewId, userId);
        String sqlReviews = "UPDATE \"reviews\" SET USEFUL = USEFUL - 1 WHERE REVIEW_ID = ?;";
        jdbcTemplate.update(sqlReviews, reviewId);
        log.info("Отзыву id={} поставил дизлайк пользователь id={}", reviewId, userId);
        log.info("измененный отзыв" + getReviewById(reviewId));
    }

    @Override
    public void deleteLike(long reviewId, long userId) {
        String sql = "DELETE FROM \"review_likes\" WHERE REVIEW_ID = ? AND USER_ID = ?";
        jdbcTemplate.update(sql, reviewId, userId);
        String sqlReviews = "UPDATE \"reviews\" SET USEFUL = USEFUL - 1 WHERE REVIEW_ID = ?;";
        jdbcTemplate.update(sqlReviews, reviewId);
        log.info("Отзыву id={} удалили лайк пользователя id={}", reviewId, userId);
        log.info("измененный отзыв" + getReviewById(reviewId));
    }

    @Override
    public void deleteDislike(long reviewId, long userId) {
        String sql = "DELETE FROM \"review_likes\" WHERE REVIEW_ID = ? AND USER_ID = ?";
        jdbcTemplate.update(sql, reviewId, userId);
        String sqlReviews = "UPDATE \"reviews\" SET USEFUL = USEFUL + 1 WHERE REVIEW_ID = ?;";
        jdbcTemplate.update(sqlReviews, reviewId);
        log.info("Отзыву id={} удалили дизлайк пользователя id={}", reviewId, userId);
        log.info("измененный отзыв" + getReviewById(reviewId));
    }

    private Review mapRowToReview(ResultSet resultSet, int rowNum) throws SQLException {
        return Review.builder()
                .reviewId(resultSet.getLong("REVIEW_ID"))
                .content(resultSet.getString("CONTENT").strip())
                .isPositive(resultSet.getBoolean("IS_POSITIVE"))
                .userId(resultSet.getLong("USER_ID"))
                .filmId(resultSet.getLong("FILM_ID"))
                .useful(resultSet.getInt("USEFUL"))
                .build();
    }
}
