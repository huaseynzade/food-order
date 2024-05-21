package org.wolt.woltproject.controllers;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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

    @Operation(
            summary = "Create a item for menu",
            description = "You can create a item and add that to menu"
    )
    @PostMapping
    public void create(@RequestBody @Valid MenuItemDto dto){
        service.create(dto);
    }

    @Operation(
            summary = "Update Menu Item",
            description = "You can update a menu item by id with this method."
    )
    @PutMapping("/{id}")
    public void update(@RequestBody @Valid MenuItemDto dto, @PathVariable Integer id) {
        service.update(dto, id);
    }

    @Operation(
            summary = "Show Content off Menu",
            description = "You can see all the items of menu with this method"
    )
    @GetMapping("/{menuId}/all")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<HashMap<String, Object>> getAll(@PathVariable Integer menuId,@RequestParam(defaultValue = "0") Integer page) {
        return service.getAll(menuId,page);
    }

    @Operation(
            summary = "Show Filtered Content of Menu",
            description = "You can see items of menu with filtered keyword"
    )
    @GetMapping("/filtered/{menuId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<HashMap<String,Object>> findAllFiltered(@PathVariable Integer menuId,
                                                                  @RequestParam String word,
                                                                  @RequestParam(defaultValue = "0") Integer page) {
        return service.findAllFiltered(menuId,word,page);
    }
    @GetMapping("/{id}")
    @Operation(
            summary = "Show Menu Item with its id",
            description = "You can see one item using its id with this method"
    )
    @ResponseStatus(HttpStatus.OK)
    public MenuItemDto getOne(@PathVariable Integer id) {
        return service.getOne(id);
    }

    @Operation(
            summary = "Delete Menu Item with its id",
            description = "You can see delete the item with use its id"
    )
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Integer id) {
        service.delete(id);
    }

}
