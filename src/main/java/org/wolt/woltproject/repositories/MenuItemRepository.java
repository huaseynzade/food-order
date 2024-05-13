package org.wolt.woltproject.repositories;

import jakarta.persistence.criteria.CriteriaBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.wolt.woltproject.entities.MenuEntity;
import org.wolt.woltproject.entities.MenuItemsEntity;

import java.util.List;

public interface MenuItemRepository extends CrudRepository<MenuItemsEntity, Integer> {
    Page<MenuItemsEntity> findAllByMenu(MenuEntity menu, Pageable pageable);
    Page<MenuItemsEntity> findByNameContainsIgnoreCaseAndMenu(String word, Pageable pageable, MenuEntity menu);
}
