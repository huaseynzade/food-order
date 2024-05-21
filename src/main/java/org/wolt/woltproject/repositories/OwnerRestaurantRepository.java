package org.wolt.woltproject.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.wolt.woltproject.entities.OwnerRestaurantEntity;
import org.wolt.woltproject.entities.RestaurantEntity;
import org.wolt.woltproject.entities.UserEntity;

import java.util.Optional;

@Repository
public interface OwnerRestaurantRepository extends CrudRepository<OwnerRestaurantEntity,Integer> {
    Optional<OwnerRestaurantEntity> findByOwner(UserEntity user);
    Optional<OwnerRestaurantEntity> findByRestaurant(RestaurantEntity restaurant);

    Boolean existsByOwner(UserEntity user);
    void deleteByRestaurant(RestaurantEntity restaurant);
}
