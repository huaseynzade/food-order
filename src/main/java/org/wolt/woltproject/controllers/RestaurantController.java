package org.wolt.woltproject.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tags;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.wolt.woltproject.entities.FilesEntity;
import org.wolt.woltproject.models.MenuDto;
import org.wolt.woltproject.models.RestaurantDto;
import org.wolt.woltproject.services.RestaurantService;

import java.io.IOException;
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
    public void save(@RequestBody @Valid RestaurantDto dto){
        service.save(dto);
    }

    @Operation(
            summary = "Get All Restaurants",
            description = "Show All Restaurants"
    )
    @GetMapping
    public ResponseEntity<Map<String,Object>> getAll(@RequestParam(defaultValue = "0") Integer page){
        return service.getAll(page);
    }


    @Operation(
            summary = "Get Restaurants Filtered",
            description = "Show All Restaurants that contains your keyword (filter)"
    )
    @GetMapping("/filtered")
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
    public void update(@RequestBody @Valid RestaurantDto dto,@PathVariable Integer id, HttpServletRequest request){
        service.update(dto, id, request);
    }
    @Operation(summary = "Delete restaurant")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(HttpServletRequest request,@PathVariable Integer id) {
        service.delete(request,id);
    }


    @PostMapping(value = "/file/", consumes = {"multipart/form-data"})
    public String sendFile(HttpServletRequest request,@RequestPart("file") MultipartFile file, Integer restaurantId) throws IOException {
        return service.sendFile(request, file,restaurantId);
    }

    @GetMapping(value = "/file/{id}")
    public  ResponseEntity<byte[]> showFile(@PathVariable Integer id, HttpSession session) {
        return service.showFile(id,session);
    }

    @Operation(summary = "To Authorize A User as Restaurant")
    @PutMapping("/authorize/")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void confirm(HttpServletRequest request) {
        service.confirm(request);
    }

}
