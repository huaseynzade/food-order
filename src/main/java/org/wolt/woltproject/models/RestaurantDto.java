package org.wolt.woltproject.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.wolt.woltproject.entities.MenuEntity;

import java.time.LocalDate;

@Data
public class RestaurantDto {
    public RestaurantDto(){
        creationDate = LocalDate.now();
    }
    private Integer restaurantId;
    private String name;
    private String description;
    private String address;
    private String phoneNumber;
    @JsonIgnore
    private LocalDate creationDate;
    //Method
    private double rating;
//    private MenuDto menu;
}
