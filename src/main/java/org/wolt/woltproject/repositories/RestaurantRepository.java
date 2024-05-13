package org.wolt.woltproject.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.wolt.woltproject.entities.RestaurantEntity;

import java.util.List;
import java.util.Optional;

public interface RestaurantRepository extends CrudRepository<RestaurantEntity,Integer> {
    List findAll();
    Page<RestaurantEntity> findAll(Pageable pageable);
    Optional findByRestaurantId(Integer restaurantId);


    Page<RestaurantEntity> findByNameContainsIgnoreCase(String word, Pageable pageable);



}
