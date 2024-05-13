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
import org.wolt.woltproject.entities.RestaurantEntity;
import org.wolt.woltproject.exceptions.NotFoundException;
import org.wolt.woltproject.maps.MenuMap;
import org.wolt.woltproject.maps.RestaurantMap;
import org.wolt.woltproject.models.MenuDto;
import org.wolt.woltproject.models.RestaurantDto;
import org.wolt.woltproject.repositories.MenuRepository;
import org.wolt.woltproject.repositories.RestaurantRepository;
import org.wolt.woltproject.repositories.ReviewRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class RestaurantService {
    private final RestaurantRepository repository;
    private final RestaurantMap map;
    private final ReviewRepository reviewRepository;
    private final MenuMap menuMap;
    private final MenuRepository menuRepo;

    public void save(RestaurantDto dto){
        log.info("Create Restaurant method is working");
        RestaurantEntity entity = map.toEntity(dto);
        repository.save(entity);
        log.info("Create Restaurant method is stopped. Restaurant created with id {}",entity.getRestaurantId());
    }

    public ResponseEntity<Map<String,Object>> getAll(Integer page){
        log.info("ActionLog.getAll method is started");
        Integer size = 3;

        Pageable paging = PageRequest.of(page,size);
        Page<RestaurantEntity> pageRestaurants;

//        if (word == null){
            pageRestaurants = repository.findAll(paging);
//        }else{
//            pageRestaurants = repository.findByNameContains(word,paging);
//        }
        List<RestaurantDto> dto = pageRestaurants.get().toList().stream().map(map::toDto).toList();

        Map<String,Object> response = new HashMap<>();
        response.put("Restaurants", dto);
        response.put("Page", pageRestaurants.getNumber());
        response.put("Total Pages", pageRestaurants.getTotalPages());

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    public ResponseEntity<Map<String,Object>> getAllFiltered(String word, Integer page){
        log.info("ActionLog.getAllFiltered method is started");
        Integer size = 3;

        Pageable paging = PageRequest.of(page,size);
        Page<RestaurantEntity> pageRestaurants;

        if (word == null){
        pageRestaurants = repository.findAll(paging);
        }else{
            pageRestaurants = repository.findByNameContainsIgnoreCase(word,paging);
            if (pageRestaurants.isEmpty()){
                throw new NotFoundException("Not Found any restaurants for your search");
            }
        }
        List<RestaurantDto> dto = pageRestaurants.get().toList().stream().map(map::toDto).toList();

        Map<String,Object> response = new HashMap<>();
        response.put("Restaurants", dto);
        response.put("Current Page", pageRestaurants.getNumber());
        response.put("Total Pages", pageRestaurants.getTotalPages());

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    public RestaurantDto getById(Integer id){
        log.info("Get Restraunt by id is started");
        if (!repository.existsById(id)){
            throw new NotFoundException("NOT FOUND");
        }
        return map.toDto(repository.findById(id).get());
    }
    public void update(RestaurantDto dto, Integer id){
        log.info("{} id restaurant is updating", id);
        if (!repository.existsById(id)){
            throw new NotFoundException("Not Found");
        }
        RestaurantEntity entity = map.toEntity(dto);
        entity.setRestaurantId(id);
        repository.save(entity);
        log.info("{} id restaurant is updated", id);
    }
    public void delete(Integer id){
        log.info("Delete method is working for restaurant id {}", id);
        if (!repository.existsById(id)){
            throw new NotFoundException("Not Found");
        }
        repository.deleteById(id);
        log.info("Deleted restaurant {}", id);
    }
}
