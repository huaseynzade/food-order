package org.wolt.woltproject.controllers;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.wolt.woltproject.entities.MenuItemsEntity;
import org.wolt.woltproject.models.MenuDto;
import org.wolt.woltproject.services.MenuService;

import java.util.List;

@RestController
@RequestMapping("/menu")
@RequiredArgsConstructor
public class MenuController {
    private final MenuService service;
    @GetMapping
    public List getAllMenus(){
        return service.getAllMenus();
    }

    @GetMapping("/get/{menuId}")
    public MenuDto getMenu(@PathVariable Integer menuId){
        return service.getMenu(menuId);
    }

    @PostMapping
    @Operation(
            summary = "Create a Menu",
            description = "You can create a new menu with this method. After create it you can add this menu to a restaurant"
    )
    public void createMenu(@RequestBody MenuDto menuDto){
        service.createMenu(menuDto);
    }

    @PutMapping("/update/{menuId}")
    public void updateMenu(
            @PathVariable Integer menuId,
            @RequestBody MenuDto dto){
        service.updateMenu(menuId, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        service.delete(id);
    }

    @GetMapping("/items/{id}")
    public List<MenuItemsEntity> getMenuItems(@PathVariable Integer id) {
        return service.getMenuItems(id);
    }

}
