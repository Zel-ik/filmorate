package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.entity.Review;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.ReviewNotFoundException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.service.ReviewService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ReviewController.class)
public class ReviewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReviewService reviewService;

    @Autowired
    private ObjectMapper objectMapper;

    @SneakyThrows
    @Test
    public void addReview_whenInvokedWithValidBody_thenReturnOk() {
        Review review = Review.builder().reviewId(1L).content("content").filmId(1L).userId(2L).build();
        when(reviewService.addReview(review)).thenReturn(review);
        String response = mockMvc.perform(post("/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(review)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        verify(reviewService, atLeast(1)).addReview(review);
        assertEquals(response, objectMapper.writeValueAsString(review));
    }

    @SneakyThrows
    @Test
    public void addReview_whenInvokedWithInvalidUserId_thenReturnUserNotFound() {
        Review review = Review.builder().reviewId(1L).content("content").filmId(1L).userId(2L).build();
        when(reviewService.addReview(review)).thenThrow(UserNotFoundException.class);
        mockMvc.perform(post("/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(review)))
                .andExpect(status().isNotFound())
                .andReturn()
                .getResponse()
                .getContentAsString();
        verify(reviewService, atLeast(1)).addReview(review);
    }

    @SneakyThrows
    @Test
    public void addReview_whenInvokedWithInvalidFilmId_thenReturnUserNotFound() {
        Review review = Review.builder().reviewId(1L).content("content").filmId(1L).userId(2L).build();
        when(reviewService.addReview(review)).thenThrow(FilmNotFoundException.class);
        mockMvc.perform(post("/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(review)))
                .andExpect(status().isNotFound())
                .andReturn()
                .getResponse()
                .getContentAsString();
        verify(reviewService, atLeast(1)).addReview(review);
    }

    @SneakyThrows
    @Test
    public void getReviewById_whenInvokedWithValidId_thenReturnOkWithReview() {
        Review review = Review.builder().reviewId(1L).content("content").filmId(1L).userId(2L).build();
        when(reviewService.getReviewById(1L)).thenReturn(review);
        String response = mockMvc.perform(get("/reviews/1"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        verify(reviewService, atLeast(1)).getReviewById(1L);
        assertEquals(response, objectMapper.writeValueAsString(review));
    }

    @SneakyThrows
    @Test
    public void getReviewById_whenInvokedWithInvalidId_thenReturnNotFound() {
        when(reviewService.getReviewById(Mockito.anyLong())).thenThrow(ReviewNotFoundException.class);
        mockMvc.perform(get("/reviews/1"))
                .andExpect(status().isNotFound())
                .andReturn()
                .getResponse()
                .getContentAsString();
        verify(reviewService, atLeast(1)).getReviewById(Mockito.anyLong());
    }

    @SneakyThrows
    @Test
    public void updateReview_whenInvokedWithValidBody_thenReturnOkWithReview() {
        Review review = Review.builder().reviewId(1L).content("content").filmId(1L).userId(2L).build();
        when(reviewService.updateReview(review)).thenReturn(review);
        String response = mockMvc.perform(put("/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(review)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        verify(reviewService, atLeast(1)).updateReview(review);
        assertEquals(response, objectMapper.writeValueAsString(review));
    }

    @SneakyThrows
    @Test
    public void updateReview_whenInvokedWithInvalidBody_thenReturnNotFound() {
        Review review = Review.builder().reviewId(1L).content("content").filmId(1L).userId(2L).build();
        when(reviewService.updateReview(review)).thenThrow(ReviewNotFoundException.class);
        mockMvc.perform(put("/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(review)))
                .andExpect(status().isNotFound())
                .andReturn()
                .getResponse()
                .getContentAsString();
        verify(reviewService, atLeast(1)).updateReview(review);
    }

    @SneakyThrows
    @Test
    public void deleteReview_whenInvokedWithValidId_thenReturnOk() {
        doNothing().when(reviewService).deleteReview(1L);
        mockMvc.perform(delete("/reviews/1"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        verify(reviewService, atLeast(1)).deleteReview(1L);
    }

    @SneakyThrows
    @Test
    public void getReviews_whenInvokedWithValidFilmId_thenReturnOk() {
        List<Review> reviews = List.of(Review.builder().reviewId(1L).content("content").filmId(1L).userId(1L).build());
        when(reviewService.getByReviewsFilmId(1L, 10)).thenReturn(reviews);
        String response = mockMvc.perform(get("/reviews?filmId=1"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        verify(reviewService, atLeast(1)).getByReviewsFilmId(1L, 10);
        assertEquals(response, objectMapper.writeValueAsString(reviews));
    }

    @SneakyThrows
    @Test
    public void getReviews_whenInvokedWithInvalidFilmId_thenReturnNotFound() {
        when(reviewService.getByReviewsFilmId(1L, 10)).thenThrow(ReviewNotFoundException.class);
        mockMvc.perform(get("/reviews?filmId=1"))
                .andExpect(status().isNotFound())
                .andReturn()
                .getResponse()
                .getContentAsString();
        verify(reviewService, atLeast(1)).getByReviewsFilmId(1L, 10);
    }

    @SneakyThrows
    @Test
    public void addLike_whenInvokedWithValidIds_thenReturnOk() {
        doNothing().when(reviewService).addLike(5L, 6L);
        mockMvc.perform(put("/reviews/5/like/6"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        verify(reviewService, atLeast(1)).addLike(5L, 6L);
    }

    @SneakyThrows
    @Test
    public void deleteLike_whenInvokedWithValidIds_thenReturnOk() {
        doNothing().when(reviewService).deleteLike(5L, 6L);
        mockMvc.perform(delete("/reviews/5/like/6"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        verify(reviewService, atLeast(1)).deleteLike(5L, 6L);
    }

    @SneakyThrows
    @Test
    public void addDislike_whenInvokedWithValidIds_thenReturnOk() {
        doNothing().when(reviewService).addDislike(5L, 6L);
        mockMvc.perform(put("/reviews/5/dislike/6"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        verify(reviewService, atLeast(1)).addDislike(5L, 6L);
    }

    @SneakyThrows
    @Test
    public void deleteDislike_whenInvokedWithValidIds_thenReturnOk() {
        doNothing().when(reviewService).deleteDislike(5L, 6L);
        mockMvc.perform(delete("/reviews/5/dislike/6"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        verify(reviewService, atLeast(1)).deleteDislike(5L, 6L);
    }
}
