package org.wolt.woltproject.models;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class MenuDto {
    private Integer menuId;
    @NotBlank(message = "Name cannot be blank")
    private String name;

    @NotNull(message = "Restaurant ID cannot be null")
    private Integer restaurant_id;

}
