package org.wolt.woltproject.repositories;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.wolt.woltproject.entities.RestaurantEntity;
import org.wolt.woltproject.entities.ReviewsEntity;
import org.wolt.woltproject.entities.UserEntity;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends CrudRepository<ReviewsEntity,Integer> {
    Optional findByUserAndRestaurant(UserEntity user, RestaurantEntity restaurant);
    void deleteByUserAndRestaurant(UserEntity user,RestaurantEntity restaurant);

    List findAllByRestaurant(RestaurantEntity restaurant);
    List findAllByUser(UserEntity user);

    @Query("select avg(r.rating) from ReviewsEntity r where r.restaurant = :restaurantId")
    double sumRates(RestaurantEntity restaurantId);

}
