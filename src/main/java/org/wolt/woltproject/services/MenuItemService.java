package org.wolt.woltproject.services;

import lombok.RequiredArgsConstructor;
import org.apache.catalina.connector.Response;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.wolt.woltproject.entities.MenuEntity;
import org.wolt.woltproject.entities.MenuItemsEntity;
import org.wolt.woltproject.entities.RestaurantEntity;
import org.wolt.woltproject.exceptions.NotFoundException;
import org.wolt.woltproject.maps.MenuItemMap;
import org.wolt.woltproject.models.MenuItemDto;
import org.wolt.woltproject.repositories.MenuItemRepository;
import org.wolt.woltproject.repositories.MenuRepository;

import java.awt.*;
import java.util.HashMap;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MenuItemService {
    private final MenuItemRepository repository;
    private final MenuItemMap map;
    private final MenuRepository menuRepository;


    public void create(MenuItemDto dto) {
        Integer menuId = dto.getMenuId();
        MenuItemsEntity entity = map.toEntity(dto);
        MenuEntity menu = menuRepository.findById(menuId).get();
        entity.setMenu(menu);
        repository.save(entity);
        menuRepository.save(menu);

    }

    public void update(MenuItemDto dto, Integer id) {
        if (!repository.existsById(id)) {
            throw new NotFoundException("NOT_FOUND");
        }
        MenuItemsEntity entity = map.toEntity(dto);
        entity.setMenuItemId(id);
        repository.save(entity);
    }

    public ResponseEntity<HashMap<String, Object>> getAll(Integer menuId, Integer page) {
        Pageable paging = PageRequest.of(page, 3);
        Page<MenuItemsEntity> pageMenuItems;


        MenuEntity menu = menuRepository.findById(menuId).orElseThrow(() -> new NotFoundException("Menu Not Found"));
        pageMenuItems = repository.findAllByMenu(menu,paging);
        if (pageMenuItems.isEmpty()) {
            throw new NotFoundException("Not Found any restaurants for your search");
        }
        List<MenuItemDto> dto = pageMenuItems.stream().map(item -> map.toDto(item)).toList();

        HashMap<String,Object> response = new HashMap<>();

        response.put("Items", dto);
        response.put("Current Page", pageMenuItems.getNumber());
        response.put("Total Pages",pageMenuItems.getTotalPages());

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    public ResponseEntity<HashMap<String,Object>> findAllFiltered(Integer menuId, String word,Integer page){
        Pageable paging = PageRequest.of(page,3);
        Page<MenuItemsEntity> pageMenuItems;

        MenuEntity menu = menuRepository.findById(menuId).orElseThrow(() -> new NotFoundException("Not Found Menu"));

        if (word == null){
            pageMenuItems = repository.findAllByMenu(menu,paging);
        }else{
            pageMenuItems = repository.findByNameContainsIgnoreCaseAndMenu(word,paging,menu);
        }

        List<MenuItemDto> dto = pageMenuItems.stream().map(item -> map.toDto(item)).toList();

        HashMap<String,Object> response = new HashMap<>();

        response.put("Items", dto);
        response.put("Current Page", pageMenuItems.getNumber());
        response.put("Total Pages",pageMenuItems.getTotalPages());

        return new ResponseEntity<>(response,HttpStatus.OK);

    }

    public MenuItemDto getOne(Integer id) {
        if (!repository.existsById(id)) {
            throw new NotFoundException("NOT_FOUND");
        }
        return map.toDto(repository.findById(id).get());
    }

    public void delete(Integer id) {
        if (!repository.existsById(id)) {
            throw new NotFoundException("Not found");
        }
        repository.deleteById(id);
    }





}
