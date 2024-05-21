package org.wolt.woltproject.controllers;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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

    @Operation(
            summary = "Get Restaurant's Reviews",
            description = "Method provides you to see comments or reviews of restaurant"
    )
    @GetMapping("/{restaurantId}/all")
    public List<ReviewResponseDto> getAllByRestaurant(@PathVariable Integer restaurantId) {
        return service.getAllByRestaurant(restaurantId);
    }

    @Operation(
            summary = "Show User's all reviews",
            description = "Returns all reviews of all restaurant which the user made"
    )
    @GetMapping("/user/{userId}")
    public List<ReviewResponseDto> getAllByUser(@PathVariable Integer userId) {
        return service.getAllByUser(userId);
    }

    @Operation(
            summary = "Get User's review about a restaurant",
            description = "This method returns a user's review about one restaurant."
    )
    @GetMapping("/{restaurantId}")
    public ReviewResponseDto getReview(HttpServletRequest request,@PathVariable Integer restaurantId) {
        return service.getReview(request, restaurantId);
    }

    @Operation(
            summary = "Create Review",
            description = "If you have ordered anything you can make review about restaurant, else you can't"
    )
    @PostMapping
    public void createReview(HttpServletRequest request, @RequestBody @Valid ReviewsDto reviewsDto) {
        service.createReview(request, reviewsDto);
    }

    @Operation(
            summary = "Update Review"
    )
    @PutMapping
    public void editReview(HttpServletRequest request,@RequestBody @Valid ReviewsDto newReviewDto) {
        service.editReview(request, newReviewDto);
    }

    @Operation(
            summary = "Remove review",
            description = "You can remove your review about any restaurant with that restaurant's id"
    )
    @DeleteMapping("/{restaurantId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteReview(HttpServletRequest request,@PathVariable Integer restaurantId) {
        service.deleteReview(request, restaurantId);
    }

}
