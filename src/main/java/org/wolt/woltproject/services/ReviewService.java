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
import org.wolt.woltproject.maps.OrderMap;
import org.wolt.woltproject.maps.ReviewMap;
import org.wolt.woltproject.models.ReviewResponseDto;
import org.wolt.woltproject.models.ReviewsDto;
import org.wolt.woltproject.models.UserResponseDto;
import org.wolt.woltproject.repositories.*;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReviewService {
    private final RestaurantService restaurantService;
    private final ReviewRepository repository;
    private final UserRepository userRepository;
    private final RestaurantRepository restaurantRepository;
    private final MenuRepository menuRepository;
    private final OrderMap orderMap;
    private final OrderRepository orderRepository;
    private final ReviewMap map;
    private final JwtService jwtService;

    public List getAllByRestaurant(Integer restaurantId) {
        log.info("ReviewService.getAllByRestaurant method started for restaurant {}", restaurantId);
        RestaurantEntity restaurant = restaurantRepository.findById(restaurantId).orElseThrow(() -> new NotFoundException("Not Found Restaurant"));
        log.info("ReviewService.getAllByRestaurant method finished for restaurant {}", restaurantId);
        return repository.findAllByRestaurant(restaurant).stream().map(e -> map.toDto((ReviewsEntity) e)).toList();
    }

    public List getAllByUser(Integer userId) {
        log.info("ReviewService.getAllByUser method started by user {}", userId);
        UserEntity user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Not Found User"));
        List<UserResponseDto> userResponseDtoList = repository.findAllByUser(user).stream().map(e -> map.toDto((ReviewsEntity) e)).toList();
        log.info("ReviewService.getAllByUser method finished by user {}", userId);
        return userResponseDtoList;
    }

    public ReviewResponseDto getReview(HttpServletRequest request, Integer restaurantId) {
        Integer userId = jwtService.getUserId(jwtService.resolveClaims(request));
        log.info("ReviewService.getReview method started by user {}", userId);
        UserEntity user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Not Found User"));
        RestaurantEntity restaurant = restaurantRepository.findById(restaurantId).orElseThrow(() -> new NotFoundException("Not Found Restaurant"));
        if (repository.findByUserAndRestaurant(user, restaurant).isEmpty()) {
            throw new NotFoundException("Not Found Review");
        }
        log.info("ReviewService.getReview method finished by user {}", userId);
        return map.toDto((ReviewsEntity) repository.findByUserAndRestaurant(user, restaurant).get());
    }


    public void createReview(HttpServletRequest request, ReviewsDto reviewsDto) {
        Integer userId = jwtService.getUserId(jwtService.resolveClaims(request));
        log.info("ReviewService.createReview method started by user {}", userId);
        UserEntity user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User Not Found"));

        if (!orderRepository.existsByUserIdAndStatusIs(user, OrderStatusEnum.ARRIVED)){
            throw new CantDoReview("You have never take delivery.");
        }


        ReviewsEntity entity = map.toEntity(reviewsDto);
        Integer restaurantId = reviewsDto.getRestaurantId();
        if (repository.findByUserAndRestaurant(user, restaurantRepository.findById(restaurantId)
                        .orElseThrow(() -> new NotFoundException("Restaurant Not Found")))
                        .isPresent()){
            throw new AlreadyRated("You have already rated this restaurant");
        }
        if (!checkOrder(userId,reviewsDto.getRestaurantId())){
            throw new CantRate("You can't rate restaurant you have never ordered anything");
        }
        RestaurantEntity restaurant = restaurantRepository.findById(restaurantId).orElseThrow(() -> new NotFoundException("Restaurant Not Found"));

//        restaurant.setRating();
//        restaurantRepository.save(restaurant);

        entity.setUser(user);

        repository.save(entity);

        Double rating = repository.sumRates(restaurant);
        restaurant.setRating(rating);
        restaurantRepository.save(restaurant);
        log.info("ReviewService.createReview method finished by user {}", userId);

    }


    public void editReview(HttpServletRequest request, ReviewsDto newReviewDto) {
        Integer userId = jwtService.getUserId(jwtService.resolveClaims(request));
        log.info("ReviewService.editReview method started by user {}", userId);
        UserEntity user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User Not Found"));
        RestaurantEntity restaurant = restaurantRepository.findById(newReviewDto.getRestaurantId()).orElseThrow(() -> new NotFoundException("Restaurant Not Found"));

        ReviewsEntity oldReview = (ReviewsEntity) repository.findByUserAndRestaurant(user, restaurant).get();

        ReviewsEntity newReviewEntity = map.toEntity(newReviewDto);
        newReviewEntity.setId(oldReview.getId());
        newReviewEntity.setUser(user);

        repository.save(newReviewEntity);
        log.info("ReviewService.editReview method finished by user {}", userId);

    }

    @Transactional
    public void deleteReview(HttpServletRequest request, Integer restaurantId) {
        Integer userId = jwtService.getUserId(jwtService.resolveClaims(request));
        log.info("ReviewService.deleteReview method started by user {}", userId);
        UserEntity user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Not Found User"));
        RestaurantEntity restaurant = restaurantRepository.findById(restaurantId).orElseThrow(() -> new NotFoundException("Not Found Restaunt"));

        if (repository.findByUserAndRestaurant(user, restaurant).isEmpty()) {
            throw new NotFoundException("Not Found");
        }

        repository.deleteByUserAndRestaurant(user, restaurant);
        log.info("ReviewService.deleteReview method finished by user {}", userId);


    }

    private Boolean checkOrder(Integer userId, Integer restaurantId){
        RestaurantEntity restaurant = restaurantRepository.findById(restaurantId).get();
        MenuEntity menu = restaurant.getMenu();
        UserEntity user = userRepository.findById(userId).get();
        List<OrderEntity> list = orderRepository.findAllByUserId(user);
        for (MenuItemsEntity menuItem : menu.getItems()) {
            boolean orderedFromRestaurant = false;
            for (OrderEntity order : list) {
                List<MenuItemsEntity> orderItems = order.getItems();
                if (orderItems.stream().anyMatch(item -> item.getMenuItemId() == menuItem.getMenuItemId())) {
                    orderedFromRestaurant = true;
                    break;
                }
            }
            if (orderedFromRestaurant) {
                return true;
            }
        }
        return false;
    }

}
