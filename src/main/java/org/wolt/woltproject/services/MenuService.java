package org.wolt.woltproject.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.wolt.woltproject.entities.MenuEntity;
import org.wolt.woltproject.entities.RestaurantEntity;
import org.wolt.woltproject.exceptions.NotFoundException;
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

    public void createMenu(MenuDto menuDto) {
        log.info("ActionLog.MenuService.createMenu method is started for id {}", menuDto.getName());
        MenuEntity menu = map.toEntity(menuDto);
        RestaurantEntity restaurant = restRepo.findById(menuDto.getRestaurant_id()).orElseThrow(() -> new NotFoundException("Restaurant Not Found"));
        restaurant.setMenu(menu);
        repository.save(menu);
        restRepo.save(restaurant);
        log.info("ActionLog.MenuService.createMenu method is started for id {}", menuDto.getName());
    }

    public void updateMenu(Integer menuId, MenuDto dto) {
        log.info("ActionLog.MenuService.updateMenu method is started for id {}", menuId);
        checkEntity(menuId);
        MenuEntity menuEntity = map.toEntity(dto);
        RestaurantEntity restaurant = restRepo.findById(dto.getRestaurant_id()).orElseThrow(() -> new NotFoundException("Restaurant Not Found"));
        menuEntity.setMenuId(menuId);
        restaurant.setMenu(menuEntity);
        repository.save(menuEntity);
        restRepo.save(restaurant);
        log.info("ActionLog.MenuService.updateMenu method is finished for id {}", menuId);

    }


    public List<MenuDto> getAllMenus() {
        log.info("ActionLog.MenuService.getAllMenus method is started");
        List<MenuDto> menuDtos = repository.findAll().stream().map(e -> {
            MenuDto menuDto = map.toDto(e);
            menuDto.setRestaurant_id((e).getRestaurant().getRestaurantId());
            return menuDto;
        }).toList();
        log.info("ActionLog.MenuService.getAllMenus method is finished");
        return menuDtos;
    }

    public MenuDto getMenu(Integer id) {
        log.info("ActionLog.MenuService.getMenu method is started for id {}", id);
        MenuEntity menu = repository.findById(id).orElseThrow(() -> new NotFoundException("Menu Not Found"));
        MenuDto menuDto = map.toDto(menu);
        menuDto.setRestaurant_id(menu.getRestaurant().getRestaurantId());
        log.info("ActionLog.MenuService.getMenu method is finished for id {}", id);
        return menuDto;
    }


    public void delete(Integer id) {
        log.info("ActionLog.MenuService.delete method is started for id {}", id);
        checkEntity(id);
        repository.deleteById(id);
        log.info("ActionLog.MenuService.delete method is finished for id {}", id);

    }


    //    Seperated Methods for short main methods
    //
    //
    //
    //
    //
    //


    public void checkEntity(Integer id) {
        MenuEntity checkEntity = repository.findById(id).orElseThrow(() -> new NotFoundException("Not Found Menu"));
    }


}
