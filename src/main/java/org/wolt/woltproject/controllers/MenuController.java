package org.wolt.woltproject.controllers;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.wolt.woltproject.entities.MenuItemsEntity;
import org.wolt.woltproject.models.MenuDto;
import org.wolt.woltproject.models.MenuItemDto;
import org.wolt.woltproject.services.MenuService;

import java.util.List;

@RestController
@RequestMapping("/menu")
@RequiredArgsConstructor
public class MenuController {
    private final MenuService service;
    @Operation(
            summary = "Get All menus",
            description = "Return all the menus"
    )
    @GetMapping
    public List getAllMenus(){
        return service.getAllMenus();
    }

    @Operation(
            summary = "Get a Menu with its id",
            description = "You can get a menu by id with this method."
    )
    @GetMapping("/{menuId}")
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

    @PutMapping("/{menuId}")
    @Operation(
            summary = "Update Menu",
            description = "You can update a menu by id with this method."
    )
    public void updateMenu(
            @PathVariable Integer menuId,
            @RequestBody MenuDto dto){
        service.updateMenu(menuId, dto);
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete Menu",
            description = "You can delete a menu by id with this method."
    )
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Integer id) {
        service.delete(id);
    }


}
