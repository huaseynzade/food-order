package org.wolt.woltproject.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(schema = "final_project", name = "restaurants")
@Data
public class RestaurantEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer restaurantId;
    private String name;
    private String description;
    private String address;
    private String phoneNumber;

    @OneToOne(cascade = CascadeType.PERSIST)
    private UserEntity owner;
    private LocalDate creationDate;

    //Method
    private double rating;


    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "menu")
    private MenuEntity menu;


    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.MERGE, fetch = FetchType.LAZY)
    private List<ReviewsEntity> reviews;


}
