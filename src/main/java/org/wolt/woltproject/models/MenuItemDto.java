package org.wolt.woltproject.models;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MenuItemDto {
    @NotBlank(message = "Name cannot be blank")
    private String name;

    @NotBlank(message = "Category cannot be blank")
    private String category;

    private String description;

    @NotNull(message = "Price cannot be null")
    @PositiveOrZero(message = "Price must be positive or zero")
    private Double price;

    @NotNull(message = "Menu ID cannot be null")
    private Integer menuId;

}
