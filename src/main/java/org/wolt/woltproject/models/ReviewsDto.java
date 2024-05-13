package org.wolt.woltproject.models;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ReviewsDto {

    private Integer restaurantId;



    @Size(max = 5)
    private Double rating;

    @NotBlank
    private String comment;
}
