package org.wolt.woltproject.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.wolt.woltproject.entities.MenuEntity;
import org.wolt.woltproject.entities.RestaurantEntity;

import java.util.List;
import java.util.Optional;

public interface MenuRepository extends JpaRepository<MenuEntity,Integer> {
    List<MenuEntity> findAll();
    Optional<MenuEntity> findByRestaurant(RestaurantEntity restaurant);
}
