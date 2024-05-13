package org.wolt.woltproject.models;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ReviewResponseDto {
    private Integer restaurantId;

    private String username;

    private Double rating;

    @NotBlank
    private String comment;
}
