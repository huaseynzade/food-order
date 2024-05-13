package org.wolt.woltproject.entities;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(schema = "final_project",name = "reviews")
@Data
public class ReviewsEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "restaurant")
    private RestaurantEntity restaurant;
    private Double rating;
    private String comment;
}
