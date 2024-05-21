package org.wolt.woltproject.services;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.wolt.woltproject.entities.*;
import org.wolt.woltproject.enums.RoleEnum;
import org.wolt.woltproject.exceptions.NotFoundException;
import org.wolt.woltproject.exceptions.PdfOnlyException;
import org.wolt.woltproject.maps.RestaurantMap;
import org.wolt.woltproject.models.RestaurantDto;
import org.wolt.woltproject.repositories.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class RestaurantService {
    private final RestaurantRepository repository;
    private final RestaurantMap map;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final FilesRepository filesRepository;
    private final MenuRepository menuRepository;
    private final MenuItemRepository menuItemRepository;
    private final OwnerRestaurantRepository ownerRestaurantRepository;

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
        Page<RestaurantEntity> pageRestaurants = repository.findAll(paging);
        ResponseEntity<Map<String, Object>> responseEntity = createResponseEntity(pageRestaurants);
        log.info("ActionLog.RestaurantService.getAll method is finished");
        return responseEntity;
    }

    public Page<RestaurantEntity> checkWord(Pageable paging, String word) {
        Page<RestaurantEntity> pageRestaurants;

        if (word == null) {
            pageRestaurants = repository.findAll(paging);
        } else {
            pageRestaurants = repository.findByNameContainsIgnoreCase(word, paging);
            if (pageRestaurants.isEmpty()) {
                throw new NotFoundException("Not Found any restaurants for your search");
            }
        }

        return pageRestaurants;
    }

    public ResponseEntity<Map<String, Object>> createResponseEntity(Page<RestaurantEntity> pageRestaurants) {
        List<RestaurantDto> dto = pageRestaurants.get().toList().stream().map(map::toDto).toList();

        Map<String, Object> response = new HashMap<>();
        response.put("Restaurants", dto);
        response.put("Page", pageRestaurants.getNumber());
        response.put("Total Pages", pageRestaurants.getTotalPages());

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    public ResponseEntity<Map<String, Object>> getAllFiltered(String word, Integer page) {
        log.info("ActionLog.RestaurantService.getAllFiltered method is started for word {} ", word);
        int size = 3;
        Pageable paging = PageRequest.of(page, size);

        Page<RestaurantEntity> pageRestaurants = checkWord(paging, word);

        ResponseEntity<Map<String, Object>> responseEntity = createResponseEntity(pageRestaurants);
        log.info("ActionLog.RestaurantService.getAllFiltered method is finished");
        return responseEntity;
    }

    public RestaurantDto getById(Integer id) {
        log.info("ActionLog.RestaurantService.getById method is started for id {}", id);
        RestaurantDto dto = map.toDto(repository.findById(id).orElseThrow(() -> new NotFoundException("Menu Not Found")));
        log.info("ActionLog.RestaurantService.getById method is finished for id {}", id);
        return dto;
    }


    public void update(RestaurantDto dto, Integer id, HttpServletRequest request) {
        Integer userId = jwtService.getUserId(jwtService.resolveClaims(request));
        UserEntity user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User Not Found"));
        RestaurantEntity restaurant = repository.findById(id).orElseThrow(() -> new NotFoundException("Restaurant Not Found"));
        log.info("ActionLog.RestaurantService.update method is started for id {}", id);
        if (!repository.existsById(id)) {
            throw new NotFoundException("Not Found");
        }
        RestaurantEntity entity = map.toEntity(dto);
        entity.setRestaurantId(id);
        entity.setMenu(restaurant.getMenu());
        entity.setRating(restaurant.getRating());
        if (checkIfItsOwner(user,restaurant)){
            repository.save(entity);
        }
        log.info("ActionLog.RestaurantService.update method is finished for id {}", id);
    }



    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void delete(HttpServletRequest request, Integer id) {
        Integer userId = jwtService.getUserId(jwtService.resolveClaims(request));
        UserEntity user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User Not Found"));
        RestaurantEntity restaurant = repository.findById(id).orElseThrow(() -> new NotFoundException("Restaurant NOT FOUND"));
        log.info("ActionLog.RestaurantService.delete method is started for id {}", id);
        if (!checkIfItsOwner(user,restaurant)) {
            throw new NotFoundException("Not Found");
        }
        breakConnectivity(restaurant);
        repository.deleteById(id);
        log.info("ActionLog.RestaurantService.delete method is finished for id {}", id);
    }

    public String sendFile(HttpServletRequest request, MultipartFile file, Integer restaurantId) throws IOException {
        Integer userId = jwtService.getUserId(jwtService.resolveClaims(request));
        UserEntity user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User Not Found"));
        log.info("ActionLog.RestaurantService.sendFile is started for user id {}", userId);
        RestaurantEntity restaurant = repository.findById(restaurantId).orElseThrow(() -> new NotFoundException("Restaurant Not Found"));

        checkIfPDF(file);


        FilesEntity filesEntity = FilesEntity.builder()
                .fileName(file.getName())
                .userEntity(user)
                .restaurant(restaurant)
                .type(file.getContentType())
                .fileData(file.getBytes())
                .build();
        if (filesEntity != null) {
            filesRepository.save(filesEntity);
            log.info("ActionLog.RestaurantService.sendFile is finished for user id {} and file id is {}", userId, filesEntity.getId());
            return "File uploaded successfully";
        }
        return null;
    }


    public ResponseEntity<byte[]> showFile(Integer id, HttpSession session) {
        log.info("ActionLog.RestaurantService.showFile is started for file id {}", id);
        FilesEntity file = filesRepository.findById(id).orElseThrow(() -> new NotFoundException("File Not Found"));
        session.setAttribute("fileId", id);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentDisposition(ContentDisposition.attachment().filename(file.getFileName()).build());
        headers.setContentType(MediaType.APPLICATION_PDF);
        log.info("ActionLog.RestaurantService.showFile is finished for file id {} and user id {}", id, file.getUserEntity().getUserId());
        return ResponseEntity.status(HttpStatus.OK)
                .contentType(MediaType.valueOf("application/pdf"))
                .headers(headers)
                .body(file.getFileData());
    }


    public void confirm(HttpServletRequest request) {
        log.info("ActionLog.RestaurantService.confirm method is started");

        HttpSession session = request.getSession();
        Integer fileId = (Integer) session.getAttribute("fileId");
        FilesEntity file = filesRepository.findById(fileId).orElseThrow(() -> new NotFoundException("File Not Found"));
        log.info("ActionLog.RestaurantService.confirm method is going for user {} and restaurant {}",file.getUserEntity().getUserId(),file.getRestaurant().getRestaurantId());
        RestaurantEntity restaurant = file.getRestaurant();
        UserEntity user = file.getUserEntity();

        saveToDb(user,restaurant);


        filesRepository.deleteById(fileId);
        log.info("ActionLog.RestaurantService.confirm method is finished for user {} and restaurant {}",file.getUserEntity().getUserId(),file.getRestaurant().getRestaurantId());
    }

    public void saveToDb(UserEntity user, RestaurantEntity restaurant) {
        if (!ownerRestaurantRepository.existsByOwner(user)) {
            OwnerRestaurantEntity ownerRestaurantEntity = new OwnerRestaurantEntity();
            ownerRestaurantEntity.setOwner(user);
            ownerRestaurantEntity.setRestaurant(restaurant);
            ownerRestaurantRepository.save(ownerRestaurantEntity);
        }
    }

    public void checkIfPDF(MultipartFile file){
        if (!(Objects.equals(file.getContentType(), "application/pdf"))) {
            throw new PdfOnlyException("You can upload only pdf files. Please choose another or just convert your file to PDF.");
        }
    }
    public boolean checkIfItsOwner(UserEntity user, RestaurantEntity restaurantEntity){
        if (user.getRole().equals(RoleEnum.RESTAURANT)){
            OwnerRestaurantEntity owner = ownerRestaurantRepository.findByOwner(user).orElseThrow(() -> new NotFoundException("Owner Not Found"));
            OwnerRestaurantEntity restaurant = ownerRestaurantRepository.findByRestaurant(restaurantEntity).orElseThrow(() -> new NotFoundException("Owner Not Found"));
            return Objects.equals(owner.getId(), restaurant.getId());
        }
        return true;
    }
    public void breakConnectivity(RestaurantEntity restaurant){
        OwnerRestaurantEntity ownerRestaurantEntity = ownerRestaurantRepository.findByRestaurant(restaurant).orElseThrow();
        ownerRestaurantEntity.setRestaurant(null);
        ownerRestaurantRepository.save(ownerRestaurantEntity);
        MenuEntity menu = menuRepository.findByRestaurant(restaurant).orElseThrow(() -> new NotFoundException("Menu Not Found"));
        List<MenuItemsEntity> menuItemsEntities = menuItemRepository.findAllByMenu(menu);
        for (MenuItemsEntity menuItemsEntity : menuItemsEntities) {
            Integer id = menuItemsEntity.getMenuItemId();
            menuItemRepository.deleteById(id);
        }
        menuRepository.delete(menu);
    }
}
