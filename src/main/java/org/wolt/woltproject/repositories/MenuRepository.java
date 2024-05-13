package org.wolt.woltproject.repositories;

import org.springframework.data.repository.CrudRepository;
import org.wolt.woltproject.entities.MenuEntity;

import java.util.List;

public interface MenuRepository extends CrudRepository<MenuEntity,Integer> {
    List findAll();
}
