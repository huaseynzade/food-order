package org.wolt.woltproject.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.wolt.woltproject.entities.MenuEntity;

import java.util.List;

public interface MenuRepository extends JpaRepository<MenuEntity,Integer> {
    List<MenuEntity> findAll();
}
