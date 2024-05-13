package org.wolt.woltproject.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.wolt.woltproject.entities.MenuEntity;
import org.wolt.woltproject.entities.MenuItemsEntity;
import org.wolt.woltproject.entities.RestaurantEntity;
import org.wolt.woltproject.exceptions.NotFoundException;
import org.wolt.woltproject.maps.MenuItemMap;
import org.wolt.woltproject.maps.MenuMap;
import org.wolt.woltproject.models.MenuDto;
import org.wolt.woltproject.repositories.MenuRepository;
import org.wolt.woltproject.repositories.RestaurantRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MenuService {
    private final MenuMap map;
    private final MenuRepository repository;
    private final RestaurantRepository restRepo;
    private final MenuItemMap itemMap;
    public void createMenu(MenuDto menuDto) {
        log.info(String.valueOf(menuDto.getRestaurant_id()));
        MenuEntity menu = map.toEntity(menuDto);
        if (!restRepo.existsById(menuDto.getRestaurant_id())) {
            throw new NotFoundException("Not Found");
        }
        RestaurantEntity restaurant = restRepo.findById(menuDto.getRestaurant_id()).get();
        restaurant.setMenu(menu);

        repository.save(menu);
        restRepo.save(restaurant);
    }

    public void updateMenu(Integer menuId, MenuDto dto) {
        if (repository.existsById(menuId)) {
            MenuEntity menuEntity = map.toEntity(dto);
            RestaurantEntity restaurant = restRepo.findById(dto.getRestaurant_id()).get();
            menuEntity.setMenuId(menuId);
            restaurant.setMenu(menuEntity);
            menuEntity.setRestaurant(restaurant);
            repository.save(menuEntity);
            restRepo.save(restaurant);
        } else {
            throw new NotFoundException("Not Found");
        }

    }


    public List getAllMenus() {
        return repository.findAll().stream().map(e -> {
            MenuDto menuDto = map.toDto((MenuEntity) e);
            menuDto.setRestaurant_id(((MenuEntity) e).getRestaurant().getRestaurantId());
            return menuDto;
        }).toList();
    }

    public MenuDto getMenu(Integer id) {
        if (!repository.existsById(id)) {
            throw new NotFoundException("Not Found");
        }
        MenuEntity menu = repository.findById(id).get();
        MenuDto menuDto = map.toDto(menu);
        menuDto.setRestaurant_id(menu.getRestaurant().getRestaurantId());
        return menuDto;
    }

    public List getMenuItems(Integer id) {
        if (!repository.existsById(id)) {
            throw new NotFoundException("Not Found");
        }
        var entityList = repository.findById(id).get().getItems();
        return entityList.stream().map(e -> {
            var eDto = itemMap.toDto(e);
            eDto.setMenuId(e.getMenu().getMenuId());
            return eDto;
        }).toList();

    }

    public void delete(Integer id) {
        if (!repository.existsById(id)) {
            throw new NotFoundException("Not found");
        }
        repository.deleteById(id);
    }
}
