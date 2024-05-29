package org.wolt.woltproject.services;

import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.wolt.woltproject.config.mail.MailConfig;
import org.wolt.woltproject.entities.*;
import org.wolt.woltproject.enums.OrderStatusEnum;
import org.wolt.woltproject.exceptions.*;
import org.wolt.woltproject.maps.OrderMap;
import org.wolt.woltproject.models.*;
import org.wolt.woltproject.repositories.*;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {
    private final OrderRepository repository;
    private final UserRepository userRepository;
    private final MenuItemRepository itemRepository;
    private final RestaurantRepository restaurantRepository;
    private final OrderMap map;
    private final JwtService jwtService;
    private final OwnerRestaurantRepository ownerRestaurantRepository;
    private final MailConfig mailConfig;
    private final GeocodingService geocodingService;


    public void addOrCreateOrder(HttpServletRequest request, Integer item) {
        Integer userId = jwtService.getUserId(jwtService.resolveClaims(request));
        log.info("ActionLog.OrderService.addOrCreateOrder method is started for id {}", userId);
        UserEntity userEntity = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Not Found User"));
        if (!userEntity.isActivated()) throw new ActivationException("Activate your account");
        OrderEntity orderEntity = findCurrentOrderOrCreateOne(userEntity);
        addItemToOrderAndCalculateTotal(item, orderEntity);
        log.info("ActionLog.OrderService.addOrCreateOrder method is finished for id {}", userId);
    }

    public OrderResponseDto getOrder(HttpServletRequest request) {
        Integer userId = jwtService.getUserId(jwtService.resolveClaims(request));
        log.info("ActionLog.OrderService.getOrderList method is started for id {}", userId);
        UserEntity user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User Not Found"));

        OrderEntity order = repository.findByStatusAndUserId(OrderStatusEnum.PENDING, user)
                .orElseThrow(() -> new NotFoundException("Not Found any Order"));

        OrderResponseDto dto = createResponseDto(order);
        log.info("ActionLog.OrderService.getOrderList method is finished for id {} and order {}", userId, dto);
        return dto;
    }

    public List<OrderResponseDto> getPastOrders(HttpServletRequest request) {
        Integer userId = jwtService.getUserId(jwtService.resolveClaims(request));
        log.info("ActionLog.OrderService.getPastOrders method is started for id {}", userId);
        UserEntity user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User Not Found"));
        List<OrderEntity> orderList = repository.findAllByUserIdAndStatusIsNot(user, OrderStatusEnum.PENDING);
        List<OrderResponseDto> list = orderList.stream().map(map::toDto).toList();
        log.info("ActionLog.OrderService.getPastOrders method is finished for id {}", userId);
        return list;
    }

    @Transactional
    public void deleteItem(HttpServletRequest request, Integer itemId) {
        Integer userId = jwtService.getUserId(jwtService.resolveClaims(request));
        log.info("ActionLog.OrderService.deleteItem method is started for id {}", userId);
        UserEntity user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User Not Found"));
        OrderEntity order = repository.findByStatusAndUserId(OrderStatusEnum.PENDING, user).orElseThrow(() -> new NotFoundException("Order Not Found"));
        if (!removeFromOrderAndUpdatePrice(order, itemId)) {
            throw new NotFoundException("There is no item like that");
        }
        repository.save(order);

        deleteOrderIfEmpty(order, user);
        log.info("ActionLog.OrderService.deleteItem method is finished for id {} deleted item id {}", userId, itemId);
    }

    public void clear(Integer userId) {
        log.info("ActionLog.OrderService.clear method is started for id {}", userId);
        UserEntity user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Not Found User"));
        OrderEntity order = repository.findByStatusAndUserId(OrderStatusEnum.PENDING, user).orElseThrow(() -> new NotFoundException("Order Not Found"));
        if (order.getItems().isEmpty()) {
            throw new NotFoundException("Not Found any Order");
        }


        user.getOrderEntity().remove(order);

        userRepository.save(user);
        log.info("ActionLog.OrderService.clear method is finished for id {}", userId);
    }

    public void prepareOrder(HttpServletRequest request, Integer orderId) {
        Integer id = jwtService.getUserId(jwtService.resolveClaims(request));
        UserEntity userEntity = userRepository.findById(id).orElseThrow(() -> new NotFoundException("User Not Found"));
        OwnerRestaurantEntity owner = ownerRestaurantRepository.findByOwner(userEntity).orElseThrow(() -> new VerifyYouOwner("You aren't a owner yet. Please Verify it."));
        Integer restaurantId = owner.getRestaurant().getRestaurantId();
        log.info("ActionLog.OrderService.prepareOrder method is started for id {}", restaurantId);
        OrderEntity order = repository.findById(orderId).orElseThrow(() -> new NotFoundException("Not Found Order"));
        checkOrderRestaurant(request,order);
        order.setStatus(OrderStatusEnum.PREPARING);
        repository.save(order);

        log.info("ActionLog.OrderService.prepareOrder method is finished for id {}", restaurantId);

    }

    public void checkOrderRestaurant(HttpServletRequest request, OrderEntity order){
        OrderResponseDto orderResponseDto = map.toDto(order);
        if (!showOrdersRestaurant(request).contains(orderResponseDto))
                throw new NotAccessibleMethod("You can't prepare this order");
    }

    public List<OrderCourierDto> showOrdersCourier(){
        List<OrderEntity> orders = repository.findAllByStatusIs(OrderStatusEnum.PREPARING);
        return orders.stream().map((e) -> {
            OrderCourierDto x = map.toCourierDto(e);
            if (e.getItems() != null && !e.getItems().isEmpty())
                x.setRestaurantName(e.getItems().get(0).getMenu().getRestaurant().getName());
            return x;
        }).toList();
    }





    public List<OrderResponseDto> showOrdersRestaurant(HttpServletRequest request){
        Integer ownerId = jwtService.getUserId(jwtService.resolveClaims(request));
        UserEntity user = userRepository.findById(ownerId).orElseThrow();
        RestaurantEntity restaurant = ownerRestaurantRepository.findByOwner(user).orElseThrow().getRestaurant();
        log.info("Started for {}", ownerId);
        List<OrderEntity> order = repository.findAllByStatusIs(OrderStatusEnum.ACCEPTED);
        log.info("Order Entity list size is {}", order.size());
//        List<OrderResponseDto> orderResponseDto = order.stream().map(map::toDto).toList();
        log.info("Order Response Dto list size is {}", order.size());
         List<OrderEntity> orderEntityToMap = new ArrayList<>();
//        log.info(orderEntityToMap.toString());
        log.info("Return Dto list size is {}", order.size());

        for(OrderEntity orderEntity:order){
           log.info(String.valueOf(restaurant.getMenu().getMenuId()));
            log.info(String.valueOf(restaurant.getMenu().getRestaurant().getName()));

           log.info(String.valueOf(orderEntity.getItems().get(0).getMenu().getMenuId()));
           log.info(String.valueOf(orderEntity.getItems().get(0).getMenu().getRestaurant().getName()));
            if (Objects.equals(orderEntity.getItems().get(0).getMenu().getMenuId(), restaurant.getMenu().getMenuId())){
                orderEntityToMap.add(orderEntity);
            }
        }

        log.info(String.valueOf(orderEntityToMap.size()));
        return orderEntityToMap.stream().map(map::toDto).toList();
    }

    public String takeOrder(HttpServletRequest request, Integer orderId) throws IOException {
        log.info("ActionLog.OrderService.takeOrder method is started for id {}", orderId);
        Integer courierId = jwtService.getUserId(jwtService.resolveClaims(request));
        UserEntity userEntity = userRepository.findById(courierId).orElseThrow(() -> new NotFoundException("Courier Not Found"));
        OrderEntity order = repository.findById(orderId).orElseThrow(() -> new NotFoundException("Order Not Found"));
        log.info("ActionLog.OrderService.takeOrder courierId {} is run this method", courierId);

        checkOrderIsPreparingAndTake(order);

        CourierDetailsDto courier = CourierDetailsDto.builder().id(userEntity.getUserId()).
                name(userEntity.getFullName()).
                phoneNumber(userEntity.getPhoneNumber())
                .build();

        sendMailCourierInfo(order, courier);
        log.info("ActionLog.OrderService.takeOrder method is finished for id {}", orderId);
        return createRouteInfo(order);
    }


    public boolean deliverOrder(Integer orderId) {
        log.info("ActionLog.OrderService.deliverOrder method is started for id {}", orderId);
        OrderEntity order = repository.findById(orderId).orElseThrow(() -> new NotFoundException("Order Not Found"));
        if (order.getStatus().equals(OrderStatusEnum.IN_COURIER)) {
            order.setStatus(OrderStatusEnum.ARRIVED);
            repository.save(order);
            log.info("ActionLog.OrderService.deliverOrder method is finished for id {}", orderId);
            return true;
        } else {
            throw new NotFoundException("Order is not 'IN_COURIER' status");
        }
    }


//     Seperated Methods for short main methods

    public OrderEntity findCurrentOrderOrCreateOne(UserEntity userEntity) {
        OrderEntity orderEntity;
        Optional<OrderEntity> pendingOrder = repository.findByStatusAndUserId(OrderStatusEnum.PENDING, userEntity);
        if (pendingOrder.isEmpty()) {
            orderEntity = new OrderEntity();
            orderEntity.setUserId(userEntity);
        } else {
            orderEntity = pendingOrder.get();
        }
        return orderEntity;
    }

    public void addItemToOrderAndCalculateTotal(Integer item, OrderEntity orderEntity) {
        MenuItemsEntity addedItem = itemRepository.findById(item).orElseThrow(() -> new NotFoundException("Item Not Found"));

        if (!orderEntity.getItems().isEmpty()) {
            if (!(addedItem.getMenu() == orderEntity.getItems().get(0).getMenu())) {
                throw new MoreThanOneRestaurant(" you can't order from 2 different restaurants at same time");
            }
        }
        orderEntity.getItems().add(addedItem);
        orderEntity.setTotalAmount(orderEntity.getTotalAmount() + addedItem.getPrice());
        repository.save(orderEntity);
    }

    public OrderResponseDto createResponseDto(OrderEntity order) {
        OrderResponseDto dto = map.toDto(order);
        for (int i = 0; i < order.getItems().size(); i++) {
            dto.getItems().get(i).setMenuId(order.getItems().get(i).getMenu().getMenuId());
        }
        return dto;
    }

    public boolean removeFromOrderAndUpdatePrice(OrderEntity order, Integer itemId) {
        List<MenuItemsEntity> list = order.getItems();
        MenuItemsEntity item;
        boolean has = false;
        for (int i = 0; i < list.size(); i++) {
            item = list.get(i);
            if (Objects.equals(item.getMenuItemId(), itemId)) {
                order.setTotalAmount(order.getTotalAmount() - list.get(i).getPrice());
                list.remove(i);
                has = true;
                break;
            }
        }
        return has;
    }

    public void deleteOrderIfEmpty(OrderEntity order, UserEntity user) {
        if (order.getItems().isEmpty()) {
            user.getOrderEntity().remove(order);
            userRepository.save(user);
            log.info("Order {} was empty and removed from DB", order.getOrderId());
        }
    }

//    public List<OrderEntity> bringRestaurantsOrders(Integer restaurantId) {
//        List<OrderEntity> orderList = repository.findAllByStatusIs(OrderStatusEnum.ACCEPTED);
//        List<OrderEntity> restaurantOrderList = new ArrayList<>();
//        for (OrderEntity entity : orderList) {
//            List<MenuItemsEntity> items = entity.getItems();
//            if (Objects.equals(items.get(0).getMenu().getRestaurant().getRestaurantId(), restaurantId)) {
//                restaurantOrderList.add(entity);
//            }
//        }
//        return restaurantOrderList;
//    }

    public void checkOrderIsPreparingAndTake(OrderEntity order) {
        if (order.getStatus().equals(OrderStatusEnum.PREPARING)) {
            order.setStatus(OrderStatusEnum.IN_COURIER);
            repository.save(order);
        } else {
            throw new NotAccessibleMethod("Not Accessible Method");
        }
    }

    public void sendMailCourierInfo(OrderEntity order, CourierDetailsDto courier) {
        try {
            mailConfig.sendEmail(order.getUserId().getEmail(), "Order has taken by courier", "Courier Details" + courier);
        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new RuntimeException(e.getMessage());
        }

    }

    public String createRouteInfo(OrderEntity order) throws IOException {
        Integer userId = order.getUserId().getUserId();
        Integer restaurantId = order.getItems().get(0).getMenu().getRestaurant().getRestaurantId();

        RouteInfo routeInfo = geocodingService.getRouteFromUserToRestaurant(userId, restaurantId);
        double durationWithMinutes = (routeInfo.getDuration() / 60) * 2;
//        double distanceWithKm = routeInfo.getDistance()/100;
        return "Estimated time for your order is " + durationWithMinutes;
    }
}
