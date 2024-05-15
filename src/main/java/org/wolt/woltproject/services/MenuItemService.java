package org.wolt.woltproject.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.wolt.woltproject.entities.MenuEntity;
import org.wolt.woltproject.entities.MenuItemsEntity;
import org.wolt.woltproject.exceptions.NotFoundException;
import org.wolt.woltproject.maps.MenuItemMap;
import org.wolt.woltproject.models.MenuItemDto;
import org.wolt.woltproject.repositories.MenuItemRepository;
import org.wolt.woltproject.repositories.MenuRepository;

import java.util.HashMap;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MenuItemService {
    private final MenuItemRepository repository;
    private final MenuItemMap map;
    private final MenuRepository menuRepository;


    public void create(MenuItemDto dto) {
        log.info("ActionLog.MenuItemService.create method is started");
        Integer menuId = dto.getMenuId();
        MenuItemsEntity entity = map.toEntity(dto);
        MenuEntity menu = menuRepository.findById(menuId).orElseThrow(() -> new NotFoundException("Menu Not Found"));
        entity.setMenu(menu);
        repository.save(entity);
        menuRepository.save(menu);
        log.info("ActionLog.MenuItemService.create method is finished");
    }



    public void update(MenuItemDto dto, Integer id) {
        log.info("ActionLog.MenuItemService.create update is started");

        if (!repository.existsById(id)) {
            throw new NotFoundException("NOT_FOUND");
        }
        MenuItemsEntity entity = map.toEntity(dto);
        entity.setMenuItemId(id);
        repository.save(entity);
        log.info("ActionLog.MenuItemService.create method is finished");

    }


    public ResponseEntity<HashMap<String, Object>> getAll(Integer menuId, Integer page) {
        log.info("ActionLog.MenuItemService.getAll method is started");

        Pageable paging = PageRequest.of(page, 3);
        Page<MenuItemsEntity> pageMenuItems;


        MenuEntity menu = menuRepository.findById(menuId).orElseThrow(() -> new NotFoundException("Menu Not Found"));
        pageMenuItems = repository.findAllByMenu(menu, paging);
        if (pageMenuItems.isEmpty()) {
            throw new NotFoundException("Not Found any restaurants for your search");
        }
        List<MenuItemDto> dto = pageMenuItems.stream().map(map::toDto).toList();
        HashMap<String, Object> response = new HashMap<>();
        response.put("Items", dto);
        response.put("Current Page", pageMenuItems.getNumber());
        response.put("Total Pages", pageMenuItems.getTotalPages());
        ResponseEntity<HashMap<String, Object>> responseEntity = new ResponseEntity<>(response, HttpStatus.OK);
        log.info("ActionLog.MenuItemService.getAll method is finished");

        return responseEntity;
    }

    public ResponseEntity<HashMap<String, Object>> findAllFiltered(Integer menuId, String word, Integer page) {
        log.info("ActionLog.MenuItemService.findAllFiltered method is started");

        Pageable paging = PageRequest.of(page, 3);
        Page<MenuItemsEntity> pageMenuItems;

        MenuEntity menu = menuRepository.findById(menuId).orElseThrow(() -> new NotFoundException("Not Found Menu"));

        if (word == null) {
            pageMenuItems = repository.findAllByMenu(menu, paging);
        } else {
            pageMenuItems = repository.findByNameContainsIgnoreCaseAndMenu(word, paging, menu);
        }
        List<MenuItemDto> dto = pageMenuItems.stream().map(map::toDto).toList();
        HashMap<String, Object> response = new HashMap<>();
        response.put("Items", dto);
        response.put("Current Page", pageMenuItems.getNumber());
        response.put("Total Pages", pageMenuItems.getTotalPages());

        ResponseEntity<HashMap<String, Object>> responseEntity = new ResponseEntity<>(response, HttpStatus.OK);
        log.info("ActionLog.MenuItemService.findAllFiltered method is finished");
        return responseEntity;
    }

    public MenuItemDto getOne(Integer id) {
        log.info("ActionLog.MenuItemService.getOne method is started");
        if (!repository.existsById(id)) {
            throw new NotFoundException("NOT_FOUND");
        }
        MenuItemDto menuItemDto = map.toDto(repository.findById(id).orElseThrow(() -> new NotFoundException("Not Found MenuItem")));
        log.info("ActionLog.MenuItemService.getOne method is finished");
        return menuItemDto;
    }

    public void delete(Integer id) {
        log.info("ActionLog.MenuItemService.delete method is started");
        if (!repository.existsById(id)) {
            throw new NotFoundException("Not found");
        }
        repository.deleteById(id);
        log.info("ActionLog.MenuItemService.delete method is finished");

    }


}
