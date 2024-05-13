package org.wolt.woltproject.controllers;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.wolt.woltproject.models.ReviewResponseDto;
import org.wolt.woltproject.models.ReviewsDto;
import org.wolt.woltproject.services.ReviewService;

import java.util.List;

@RestController
@RequestMapping("/review")
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService service;

    @GetMapping("/all/restaurant/{restaurantId}")
    public List getAllByRestaurant(@PathVariable Integer restaurantId) {
        return service.getAllByRestaurant(restaurantId);
    }

    @GetMapping("/user/{userId}")
    public List getAllByUser(@PathVariable Integer userId) {
        return service.getAllByUser(userId);
    }

    @GetMapping("/user/restaurant/{restaurantId}")
    public ReviewResponseDto getReview(HttpServletRequest request,@PathVariable Integer restaurantId) {
        return service.getReview(request, restaurantId);
    }

    @PostMapping
    public void createReview(HttpServletRequest request, @RequestBody ReviewsDto reviewsDto) {
        service.createReview(request, reviewsDto);
    }

    @PutMapping()
    public void editReview(HttpServletRequest request,@RequestBody ReviewsDto newReviewDto) {
        service.editReview(request, newReviewDto);
    }

    @DeleteMapping("/restaurant/{restaurantId}")
    public void deleteReview(HttpServletRequest request,@PathVariable Integer restaurantId) {
        service.deleteReview(request, restaurantId);
    }

}
