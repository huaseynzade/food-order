package org.wolt.woltproject.services;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.wolt.woltproject.entities.*;
import org.wolt.woltproject.enums.OrderStatusEnum;
import org.wolt.woltproject.exceptions.AlreadyRated;
import org.wolt.woltproject.exceptions.CantDoReview;
import org.wolt.woltproject.exceptions.CantRate;
import org.wolt.woltproject.exceptions.NotFoundException;
import org.wolt.woltproject.maps.ReviewMap;
import org.wolt.woltproject.models.ReviewResponseDto;
import org.wolt.woltproject.models.ReviewsDto;
import org.wolt.woltproject.repositories.*;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReviewService {
    private final ReviewRepository repository;
    private final UserRepository userRepository;
    private final RestaurantRepository restaurantRepository;
    private final OrderRepository orderRepository;
    private final ReviewMap map;
    private final JwtService jwtService;

    public List<ReviewResponseDto> getAllByRestaurant(Integer restaurantId) {
        log.info("ActionLog.ReviewService.getAllByRestaurant method is started");
        RestaurantEntity restaurant = restaurantRepository.findById(restaurantId).orElseThrow(() -> new NotFoundException("Not Found Restaurant"));
        List<ReviewResponseDto> list = repository.findAllByRestaurant(restaurant).stream().map(e -> map.toDto((ReviewsEntity) e)).toList();
        log.info("ActionLog.ReviewService.getAllByRestaurant method is finished");
        return list;
    }

    public List<ReviewResponseDto> getAllByUser(Integer userId) {
        log.info("ActionLog.ReviewService.getAllByUser method is started");
        UserEntity user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Not Found User"));
        List<ReviewResponseDto> userResponseDtoList = repository.findAllByUser(user).stream().map(map::toDto).toList();
        log.info("ActionLog.ReviewService.getAllByUser method is finished");
        return userResponseDtoList;
    }

    public ReviewResponseDto getReview(HttpServletRequest request, Integer restaurantId) {
        Integer userId = jwtService.getUserId(jwtService.resolveClaims(request));
        log.info("ActionLog.ReviewService.getReview method is started for id {}", userId);
        UserEntity user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Not Found User"));
        RestaurantEntity restaurant = restaurantRepository.findById(restaurantId).orElseThrow(() -> new NotFoundException("Not Found Restaurant"));
        if (repository.findByUserAndRestaurant(user, restaurant).isEmpty()) {
            throw new NotFoundException("Not Found Review");
        }
        ReviewResponseDto dto = map.toDto(repository.findByUserAndRestaurant(user, restaurant).get());
        log.info("ActionLog.ReviewService.getReview method is finished for id {}", userId);
        return dto;
    }


    public void createReview(HttpServletRequest request, ReviewsDto reviewsDto) {
        Integer userId = jwtService.getUserId(jwtService.resolveClaims(request));
        log.info("ActionLog.ReviewService.createReview method finished by user {}", userId);
        
        UserEntity user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User Not Found"));

        if (!orderRepository.existsByUserIdAndStatusIs(user, OrderStatusEnum.ARRIVED)){
            throw new CantDoReview("You have never take delivery.");
        }
        ReviewsEntity entity = map.toEntity(reviewsDto);
        Integer restaurantId = reviewsDto.getRestaurantId();
        checkIfCanRate(user,reviewsDto,restaurantId);
        RestaurantEntity restaurant = restaurantRepository.findById(restaurantId).orElseThrow(() -> new NotFoundException("Restaurant Not Found"));
        entity.setUser(user);
        repository.save(entity);
        double rating = repository.sumRates(restaurant);
        restaurant.setRating(rating);
        restaurantRepository.save(restaurant);
        log.info("ActionLog.ReviewService.createReview method finished by user {}", userId);

    }




    public void editReview(HttpServletRequest request, ReviewsDto newReviewDto) {
        Integer userId = jwtService.getUserId(jwtService.resolveClaims(request));
        log.info("ActionLog.ReviewService.editReview method started by user {}", userId);
        UserEntity user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User Not Found"));
        RestaurantEntity restaurant = restaurantRepository.findById(newReviewDto.getRestaurantId()).orElseThrow(() -> new NotFoundException("Restaurant Not Found"));

        ReviewsEntity oldReview =  repository.findByUserAndRestaurant(user, restaurant).orElseThrow(() -> new NotFoundException("Review Not Found"));

        ReviewsEntity newReviewEntity = map.toEntity(newReviewDto);
        newReviewEntity.setId(oldReview.getId());
        newReviewEntity.setUser(user);

        double rating = repository.sumRates(restaurant);
        restaurant.setRating(rating);
        restaurantRepository.save(restaurant);
        repository.save(newReviewEntity);
        log.info("ActionLog.ReviewService.editReview method finished by user {}", userId);

    }

    @Transactional
    public void deleteReview(HttpServletRequest request, Integer restaurantId) {
        Integer userId = jwtService.getUserId(jwtService.resolveClaims(request));
        log.info("ActionLog.ReviewService..deleteReview method started by user {}", userId);
        UserEntity user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Not Found User"));
        RestaurantEntity restaurant = restaurantRepository.findById(restaurantId).orElseThrow(() -> new NotFoundException("Not Found Restaurant"));
        if (repository.findByUserAndRestaurant(user, restaurant).isEmpty()) {
            throw new NotFoundException("Not Found");
        }
        repository.deleteByUserAndRestaurant(user, restaurant);
        log.info("ActionLog.ReviewService.deleteReview method finished by user {}", userId);

    }

    //    Seperated Methods for short main methods
    //
    //
    //
    //
    //
    //



    private Boolean checkOrder(Integer userId, Integer restaurantId){
        log.info("ActionLog.ReviewService.checkOrder method is started");

        RestaurantEntity restaurant = restaurantRepository.findById(restaurantId).orElseThrow(() -> new NotFoundException("Restaurant Not Found"));
        MenuEntity menu = restaurant.getMenu();
        UserEntity user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User Not Found"));
        List<OrderEntity> list = orderRepository.findAllByUserId(user);
        boolean orderedFromRestaurant = false;
        for (MenuItemsEntity menuItem : menu.getItems()) {
            for (OrderEntity order : list) {
                List<MenuItemsEntity> orderItems = order.getItems();
                if (orderItems.stream().anyMatch(item -> Objects.equals(item.getMenuItemId(), menuItem.getMenuItemId()))) {
                    orderedFromRestaurant = true;
                    break;
                }
            }
        }
        log.info("ActionLog.ReviewService.checkOrder method is finished");
        return orderedFromRestaurant;
    }

    public void checkIfCanRate(UserEntity user,ReviewsDto reviewsDto, Integer restaurantId){
        if (repository.findByUserAndRestaurant(user, restaurantRepository.findById(restaurantId)
                        .orElseThrow(() -> new NotFoundException("Restaurant Not Found")))
                .isPresent()){
            throw new AlreadyRated("You have already rated this restaurant");
        }
        if (!checkOrder(user.getUserId(),reviewsDto.getRestaurantId())){
            throw new CantRate("You can't rate restaurant you have never ordered anything");
        }
    }

}
