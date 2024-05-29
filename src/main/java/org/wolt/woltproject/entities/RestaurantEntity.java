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



    private LocalDate creationDate;



    //Method
    private double rating;


    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "menu")
    private MenuEntity menu;




    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.MERGE, fetch = FetchType.LAZY)
    private List<ReviewsEntity> reviews;


    private double lat;

    private double lon;

    @Override
    public String toString() {
        return "RestaurantEntity{" +
                "restaurantId=" + restaurantId +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", address='" + address + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", creationDate=" + creationDate +
                ", rating=" + rating +
                '}';
    }
}
