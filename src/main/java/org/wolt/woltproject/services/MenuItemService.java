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
        log.info("ActionLog.MenuItemService.create method is started for item {}", dto.getName());
        Integer menuId = dto.getMenuId();
        MenuItemsEntity entity = map.toEntity(dto);
        MenuEntity menu = menuRepository.findById(menuId).orElseThrow(() -> new NotFoundException("Menu Not Found"));
        entity.setMenu(menu);
        repository.save(entity);
        log.info("ActionLog.MenuItemService.create method is finished for item {}, added to menu {}", entity.getMenuItemId(),menu.getMenuId());
    }



    public void update(MenuItemDto dto, Integer id) {
        log.info("ActionLog.MenuItemService.create update is started");
        MenuItemsEntity checkEntity = repository.findById(id).orElseThrow(() -> new NotFoundException("Item Not Found"));
        MenuItemsEntity entity = map.toEntity(dto);
        entity.setMenuItemId(id);
        repository.save(entity);
        log.info("ActionLog.MenuItemService.create method is finished");

    }


    public ResponseEntity<HashMap<String, Object>> getAll(Integer menuId, Integer page) {
        log.info("ActionLog.MenuItemService.getAll method is started");
        Pageable paging = PageRequest.of(page, 3);
        MenuEntity menu = menuRepository.findById(menuId).orElseThrow(() -> new NotFoundException("Menu Not Found"));
        Page<MenuItemsEntity> pageMenuItems = repository.findAllByMenu(menu, paging);
        ResponseEntity<HashMap<String, Object>> responseEntity = generateResponse(pageMenuItems);
        log.info("ActionLog.MenuItemService.getAll method is finished");
        return responseEntity;
    }

    public ResponseEntity<HashMap<String, Object>> findAllFiltered(Integer menuId, String word, Integer page) {
        log.info("ActionLog.MenuItemService.findAllFiltered method is started for search word {}", word);
        Pageable paging = PageRequest.of(page, 3);
        MenuEntity menu = menuRepository.findById(menuId).orElseThrow(() -> new NotFoundException("Not Found Menu"));
        Page<MenuItemsEntity> pageMenuItems = checkWord(paging,menu,word);
        ResponseEntity<HashMap<String, Object>> responseEntity = generateResponse(pageMenuItems);
        log.info("ActionLog.MenuItemService.findAllFiltered method is finished. Results count {}", pageMenuItems.getTotalElements());
        return responseEntity;
    }

    public MenuItemDto getOne(Integer id) {
        log.info("ActionLog.MenuItemService.getOne method is started");
        MenuItemDto menuItemDto = map.toDto(repository.findById(id).orElseThrow(() -> new NotFoundException("Not Found MenuItem")));
        log.info("ActionLog.MenuItemService.getOne method is finished");
        return menuItemDto;
    }

    public void delete(Integer id) {
        log.info("ActionLog.MenuItemService.delete method is started");
        MenuItemsEntity checkEntity = repository.findById(id).orElseThrow(() -> new NotFoundException("Item Not Found"));
        repository.deleteById(id);
        log.info("ActionLog.MenuItemService.delete method is finished");

    }


    //    Seperated Methods for short main methods
    //
    //
    //
    //
    //
    //

    public ResponseEntity<HashMap<String,Object>> generateResponse(Page<MenuItemsEntity> pageMenuItems){
        List<MenuItemDto> dto = pageMenuItems.stream().map(map::toDto).toList();
        HashMap<String, Object> response = new HashMap<>();
        response.put("Items", dto);
        response.put("Current Page", pageMenuItems.getNumber());
        response.put("Total Pages", pageMenuItems.getTotalPages());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    public Page<MenuItemsEntity> checkWord(Pageable paging, MenuEntity menu, String word){
        Page<MenuItemsEntity> pageMenuItems;
        if (word == null) {
            pageMenuItems = repository.findAllByMenu(menu, paging);
        } else {
            pageMenuItems = repository.findByNameContainsIgnoreCaseAndMenu(word, paging, menu);
        }
        return pageMenuItems;
    }


}
