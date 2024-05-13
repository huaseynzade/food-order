package org.wolt.woltproject.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tags;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.wolt.woltproject.models.MenuDto;
import org.wolt.woltproject.models.RestaurantDto;
import org.wolt.woltproject.services.RestaurantService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/restaurant")
@RequiredArgsConstructor
public class RestaurantController {
    private final RestaurantService service;


    @Operation(
            summary = "Create a Restaurant",
            description = "You can create a new method with this method. After create it you can add menu to this restaurant"
    )
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void save(@RequestBody RestaurantDto dto){
        service.save(dto);
    }

    @Operation(
            summary = "Get All Restaurants"
    )
    @GetMapping("/get/all")
    public ResponseEntity<Map<String,Object>> getAll(@RequestParam(required = false) Integer page){
        return service.getAll(page);
    }


    @GetMapping("/get/filtered/")
    public ResponseEntity<Map<String,Object>> getAllFiltered(@RequestParam String word,@RequestParam(defaultValue = "0") Integer page) {
        return service.getAllFiltered(word,page);
    }


    @Operation(summary = "Get Restaurant with its id")
    @ApiResponses({
            @ApiResponse(responseCode = "201",content = {@Content(schema = @Schema(implementation = RestaurantDto.class))})
    })
    @GetMapping("/{id}")
    public RestaurantDto getById(@PathVariable Integer id){
        return service.getById(id);
    }

    @Operation(summary = "Update Restaurant")
    @PutMapping("/{id}")
    public void update(@RequestBody RestaurantDto dto,@PathVariable Integer id){
        service.update(dto, id);
    }
    @Operation(summary = "Delete restaurant")
    @DeleteMapping("/{id}")
    public void delete(Integer id) {
        service.delete(id);
    }







//    @Operation(summary = "Get menu of restaurant with its id")
//    @GetMapping("/getMenuByRestaurant/{id}")
//    public MenuDto getMenu(@PathVariable Integer id){
//        return service.getMenu(id);
//    }


}
