package org.wolt.woltproject.services;

import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.jaxb.SpringDataJaxb;
import org.springframework.stereotype.Service;
import org.wolt.woltproject.config.mail.MailConfig;
import org.wolt.woltproject.entities.MenuItemsEntity;
import org.wolt.woltproject.entities.OrderEntity;
import org.wolt.woltproject.entities.UserEntity;
import org.wolt.woltproject.enums.OrderStatusEnum;
import org.wolt.woltproject.exceptions.ActivationException;
import org.wolt.woltproject.exceptions.NotFoundException;
import org.wolt.woltproject.maps.MenuItemMap;
import org.wolt.woltproject.maps.OrderMap;
import org.wolt.woltproject.models.CourierDetailsDto;
import org.wolt.woltproject.models.OrderResponseDto;
import org.wolt.woltproject.repositories.MenuItemRepository;
import org.wolt.woltproject.repositories.OrderRepository;
import org.wolt.woltproject.repositories.UserRepository;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {
    private final OrderRepository repository;
    private final UserRepository userRepository;
    private final MenuItemRepository itemRepository;
    private final OrderMap map;
    private final JwtService jwtService;
    private final MenuItemMap itemMap;

    private final MailConfig mailConfig;


    public void addOrCreateOrder(Integer userId, Integer item) {
        log.info("Order Creating for user {}", userId);


        if (!itemRepository.existsById(item)) {
            throw new NotFoundException("Not Found");
        }

        UserEntity userEntity = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Not Found User"));
        OrderEntity orderEntity;

        if (!userEntity.isActivated()) {
            throw new ActivationException("Activate your account");
        }


        if (repository.findByStatusAndUserId(OrderStatusEnum.PENDING, userEntity).isEmpty()) {
            orderEntity = new OrderEntity();
            orderEntity.setUserId(userEntity);
        } else {
            orderEntity = (OrderEntity) repository.findByStatusAndUserId(OrderStatusEnum.PENDING, userEntity).get();

            if (orderEntity.getStatus() == OrderStatusEnum.ACCEPTED) {
                orderEntity = new OrderEntity();
                orderEntity.setUserId(userEntity);
            }
        }
        MenuItemsEntity addedItem = itemRepository.findById(item).get();
        orderEntity.getItems().add(addedItem);
        orderEntity.setTotalAmount(orderEntity.getTotalAmount() + addedItem.getPrice());
        userEntity.getOrderEntity().add(orderEntity);
        repository.save(orderEntity);
        log.info("Order Created for user {}, added {}", userId, item);
    }

    public OrderResponseDto getOrderList(Integer userId) {
        log.info("Show Order by username id: {}", userId);
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User Not Found");
        }
        UserEntity user = userRepository.findById(userId).get();
        OrderEntity order;
        if (repository.findByStatusAndUserId(OrderStatusEnum.PENDING, user).isPresent()) {
            order = (OrderEntity) repository.findByStatusAndUserId(OrderStatusEnum.PENDING, user).get();
        } else {
            order = null;
        }
        if (order == null) {
            throw new NotFoundException("Not Found any Order");
        }
        OrderResponseDto dto = map.toDto(order);
        for (int i = 0; i < order.getItems().size(); i++) {
            dto.getItems().get(i).setMenuId(order.getItems().get(i).getMenu().getMenuId());
        }
        return dto;
    }

    public List<OrderResponseDto> getPastOrders(HttpServletRequest request) {
        Integer userId = jwtService.getUserId(jwtService.resolveClaims(request));
        UserEntity user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User Not Found"));
        List<OrderEntity> orderList = repository.findAllByUserIdAndStatusIsNot(user, OrderStatusEnum.PENDING);
        return orderList.stream().map(map::toDto).toList();
    }

    @Transactional
    public void deleteItem(Integer userId, Integer itemId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User Not Found");
        }
        log.info("Item delete with id {} for user id {}", itemId, userId);
        UserEntity user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User Not Found"));
        if (repository.findByUserId(userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User Not Found"))).isEmpty()) {
            throw new NotFoundException("There is not order");
        }
        OrderEntity order = (OrderEntity) repository.findByStatusAndUserId(OrderStatusEnum.PENDING, user).get();
        List<MenuItemsEntity> list = order.getItems();
        boolean has = false;
        MenuItemsEntity item;
        for (int i = 0; i < list.size(); i++) {
            item = list.get(i);
            if (item.getMenuItemId() == itemId) {
                order.setTotalAmount(order.getTotalAmount() - list.get(i).getPrice());
                list.remove(i);
                has = true;
//
                break;
            }
        }
        if (!has) {
            throw new NotFoundException("There is no item like that");
        }


        repository.save(order);
        if (order.getItems().isEmpty()) {
            user.setOrderEntity(null);
            userRepository.save(user);
            log.info("Order {} deleted", order.getOrderId());
        }
        log.info("Item {} has deleted for user {}", itemId, userId);
    }

    public void clear(Integer userId) {
        log.info("Clear Order method for User {} started", userId);
        UserEntity user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Not Found User"));
        OrderEntity order = (OrderEntity) repository.findByStatusAndUserId(OrderStatusEnum.PENDING, user).get();
        if (order.getItems().isEmpty()) {
            throw new NotFoundException("Not Found any Order");
        }
        user.setOrderEntity(null);
        userRepository.save(user);
        log.info("Clear Order method for User {} finished", userId);
    }


    public List<OrderResponseDto> showOrdersByRestaurant(Integer restaurantId) {

        List<OrderEntity> orderList = repository.findAllByStatusIs(OrderStatusEnum.ACCEPTED);
        List<OrderEntity> restaurantOrderList = new ArrayList<>();
        for (int i = 0; i < orderList.size(); i++) {
            List<MenuItemsEntity> items = orderList.get(i).getItems();
            if (Objects.equals(items.get(0).getMenu().getRestaurant().getRestaurantId(), restaurantId)) {
                restaurantOrderList.add(orderList.get(i));
            }
        }

        List<OrderResponseDto> orderDto = restaurantOrderList.stream().map(e -> map.toDto(e)).toList();
        return orderDto;
    }

    public void prepareOrder(Integer restaurantId, Integer orderId) {
        for (int i = 0; i < showOrdersByRestaurant(restaurantId).size(); i++) {
            if (Objects.equals(showOrdersByRestaurant(restaurantId).get(i).getOrderId(), orderId)) {
                OrderEntity order = repository.findById(orderId).orElseThrow(() -> new NotFoundException("Not Found Order"));
                order.setStatus(OrderStatusEnum.PREPARING);
                repository.save(order);
            }
        }
    }

    public void takeOrder(Integer courierId, Integer orderId) {
        UserEntity userEntity = userRepository.findById(courierId).orElseThrow(() -> new NotFoundException("Courier Not Found"));
        OrderEntity order = repository.findById(orderId).orElseThrow(() -> new NotFoundException("Order Not Found"));
        if (order.getStatus().equals(OrderStatusEnum.PREPARING)) {
            order.setStatus(OrderStatusEnum.IN_COURIER);
            repository.save(order);
        } else {

        }
        CourierDetailsDto courier = CourierDetailsDto.builder().id(userEntity.getUserId()).
                name(userEntity.getFullName()).
                phoneNumber(userEntity.getPhoneNumber())
                .build();

        try {
            mailConfig.sendEmail(order.getUserId().getEmail(), "Order has taken by courier", "Courier Details" + courier);
        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public boolean deliverOrder(Integer orderId) {
        OrderEntity order = repository.findById(orderId).orElseThrow(() -> new NotFoundException("Order Not Found"));
        if (order.getStatus().equals(OrderStatusEnum.IN_COURIER)) {
            order.setStatus(OrderStatusEnum.ARRIVED);
            repository.save(order);
            return true;
        } else {
            return false;
        }
    }


}
