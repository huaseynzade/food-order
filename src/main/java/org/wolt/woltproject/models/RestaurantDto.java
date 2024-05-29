package org.wolt.woltproject.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.*;
import lombok.Data;
import org.wolt.woltproject.entities.MenuEntity;

import java.time.LocalDate;

@Data
public class RestaurantDto {
    public RestaurantDto(){
        creationDate = LocalDate.now();
    }
    private Integer restaurantId;
    @NotBlank(message = "Name cannot be blank")
    @Pattern(regexp = ".{3,}", message = "Name must be at least 3 characters long")
    private String name;
    @NotEmpty
    private String description;
    @NotBlank(message = "Address cannot be blank")
    private String address;

    @NotBlank(message = "Phone number cannot be blank")
    @Pattern(regexp = "^\\+994\\d{9}$", message = "Phone number must start with +994 and be 13 digits long after that")
    private String phoneNumber;
    @JsonIgnore
    private LocalDate creationDate;
    //Method
    private double rating;

    private double lat;
    private double lon;
//    private MenuDto menu;
}
