package org.wolt.woltproject.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.wolt.woltproject.models.MenuItemDto;
import org.wolt.woltproject.services.MenuItemService;

import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/menu-item")
@RequiredArgsConstructor
public class MenuItemController {
    private final MenuItemService service;

    @PostMapping
    public void create(@RequestBody MenuItemDto dto){
        service.create(dto);
    }

    @PutMapping("/{id}")
    public void update(@RequestBody MenuItemDto dto, @PathVariable Integer id) {
        service.update(dto, id);
    }

    @GetMapping("/get/menu/{menuId}/all")
    public ResponseEntity<HashMap<String, Object>> getAll(@PathVariable Integer menuId,@RequestParam(defaultValue = "0") Integer page) {
        return service.getAll(menuId,page);
    }

    @GetMapping("/get/menu/{menuId}")
    public ResponseEntity<HashMap<String,Object>> findAllFiltered(@PathVariable Integer menuId,
                                                                  @RequestParam String word,
                                                                  @RequestParam(defaultValue = "0") Integer page) {
        return service.findAllFiltered(menuId,word,page);
    }
    @GetMapping("/get/{id}")
    public MenuItemDto getOne(@PathVariable Integer id) {
        return service.getOne(id);
    }

    @DeleteMapping("/id")
    public void delete(Integer id) {
        service.delete(id);
    }

}
