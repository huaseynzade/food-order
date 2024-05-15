package org.wolt.woltproject.services;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.wolt.woltproject.entities.RestaurantEntity;
import org.wolt.woltproject.entities.UserEntity;
import org.wolt.woltproject.exceptions.NotFoundException;
import org.wolt.woltproject.maps.RestaurantMap;
import org.wolt.woltproject.models.RestaurantDto;
import org.wolt.woltproject.repositories.RestaurantRepository;
import org.wolt.woltproject.repositories.UserRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class RestaurantService {
    private final RestaurantRepository repository;
    private final RestaurantMap map;
    private final JwtService jwtService;
    private final UserRepository userRepository;

    public void save(RestaurantDto dto) {
        log.info("ActionLog.RestaurantService.updateUser method is started for {}", dto.getName());
        RestaurantEntity entity = map.toEntity(dto);
        repository.save(entity);
        log.info("ActionLog.RestaurantService.updateUser method is finished for {}", dto.getName());
    }

    public ResponseEntity<Map<String, Object>> getAll(Integer page) {
        log.info("ActionLog.RestaurantService.getAll method is started");
        int size = 3;

        Pageable paging = PageRequest.of(page, size);
        Page<RestaurantEntity> pageRestaurants;

//        if (word == null){
        pageRestaurants = repository.findAll(paging);
//        }else{
//            pageRestaurants = repository.findByNameContains(word,paging);
//        }
        List<RestaurantDto> dto = pageRestaurants.get().toList().stream().map(map::toDto).toList();

        Map<String, Object> response = new HashMap<>();
        response.put("Restaurants", dto);
        response.put("Page", pageRestaurants.getNumber());
        response.put("Total Pages", pageRestaurants.getTotalPages());

        ResponseEntity<Map<String, Object>> responseEntity = new ResponseEntity<>(response, HttpStatus.OK);
        log.info("ActionLog.RestaurantService.getAll method is finished");
        return responseEntity;
    }

    public ResponseEntity<Map<String, Object>> getAllFiltered(String word, Integer page) {
        log.info("ActionLog.RestaurantService.getAllFiltered method is started");
        int size = 3;

        Pageable paging = PageRequest.of(page, size);
        Page<RestaurantEntity> pageRestaurants;

        if (word == null) {
            pageRestaurants = repository.findAll(paging);
        } else {
            pageRestaurants = repository.findByNameContainsIgnoreCase(word, paging);
            if (pageRestaurants.isEmpty()) {
                throw new NotFoundException("Not Found any restaurants for your search");
            }
        }
        List<RestaurantDto> dto = pageRestaurants.get().toList().stream().map(map::toDto).toList();

        Map<String, Object> response = new HashMap<>();
        response.put("Restaurants", dto);
        response.put("Current Page", pageRestaurants.getNumber());
        response.put("Total Pages", pageRestaurants.getTotalPages());
        ResponseEntity<Map<String, Object>> responseEntity = new ResponseEntity<>(response, HttpStatus.OK);
        log.info("ActionLog.RestaurantService.getAllFiltered method is finished");
        return responseEntity;
    }

    public RestaurantDto getById(Integer id) {
        log.info("ActionLog.RestaurantService.getById method is started");
        if (!repository.existsById(id)) {
            throw new NotFoundException("NOT FOUND");
        }
        RestaurantDto dto = map.toDto(repository.findById(id).orElseThrow(() -> new NotFoundException("Menu Not Found")));
        log.info("ActionLog.RestaurantService.getById method is finished");
        return dto;
    }

    public void update(RestaurantDto dto, Integer id) {
        log.info("ActionLog.RestaurantService.update method is started for id {}", id);
        if (!repository.existsById(id)) {
            throw new NotFoundException("Not Found");
        }
        RestaurantEntity entity = map.toEntity(dto);
        entity.setRestaurantId(id);
        repository.save(entity);
        log.info("ActionLog.RestaurantService.update method is finished for id {}", id);
    }

    public void delete(Integer id) {
        log.info("ActionLog.RestaurantService.delete method is started for id {}", id);
        if (!repository.existsById(id)) {
            throw new NotFoundException("Not Found");
        }
        repository.deleteById(id);
        log.info("ActionLog.RestaurantService.delete method is finished for id {}", id);
    }

    public String sendFile(){
        return "test stage";
    }

    public void confirm(HttpServletRequest request, Integer restaurantId){
        Integer id = jwtService.getUserId(jwtService.resolveClaims(request));
        UserEntity entity = userRepository.findById(id).orElseThrow(() -> new NotFoundException("User Not Found"));
        RestaurantEntity restaurant = repository.findById(restaurantId).orElseThrow(() -> new NotFoundException("Restaurant Not Found"));
        entity.setRestaurant(restaurant);
        userRepository.save(entity);
    }



}
