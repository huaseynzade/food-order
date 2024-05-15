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
        if (!restRepo.existsById(menuDto.getRestaurant_id())) {
            throw new NotFoundException("Not Found");
        }
        RestaurantEntity restaurant = restRepo.findById(menuDto.getRestaurant_id()).orElseThrow(() -> new NotFoundException("Restaurant Not Found"));
        restaurant.setMenu(menu);

        repository.save(menu);
        restRepo.save(restaurant);
        log.info("ActionLog.MenuService.createMenu method is started for id {}",menuDto.getName());
    }

    public void updateMenu(Integer menuId, MenuDto dto) {
        log.info("ActionLog.MenuService.updateMenu method is started for id {}", menuId);
        if (repository.existsById(menuId)) {
            MenuEntity menuEntity = map.toEntity(dto);
            RestaurantEntity restaurant = restRepo.findById(dto.getRestaurant_id()).orElseThrow(() -> new NotFoundException("Restaurant Not Found"));
            menuEntity.setMenuId(menuId);
            restaurant.setMenu(menuEntity);
            menuEntity.setRestaurant(restaurant);
            repository.save(menuEntity);
            restRepo.save(restaurant);
        } else {
            throw new NotFoundException("Not Found");
        }
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
        log.info("ActionLog.MenuService.getMenu method is started");
        if (!repository.existsById(id)) {
            throw new NotFoundException("Not Found");
        }
        MenuEntity menu = repository.findById(id).orElseThrow(() -> new NotFoundException("Menu Not Found"));
        MenuDto menuDto = map.toDto(menu);
        menuDto.setRestaurant_id(menu.getRestaurant().getRestaurantId());
        log.info("ActionLog.MenuService.getMenu method is finished");
        return menuDto;
    }



    public void delete(Integer id) {
        log.info("ActionLog.MenuService.delete method is started");

        if (!repository.existsById(id)) {
            throw new NotFoundException("Not found");
        }
        repository.deleteById(id);
        log.info("ActionLog.MenuService.delete method is finished");

    }
}
