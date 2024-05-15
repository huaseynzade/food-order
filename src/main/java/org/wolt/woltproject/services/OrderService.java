package org.wolt.woltproject.services;

import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.wolt.woltproject.config.mail.MailConfig;
import org.wolt.woltproject.entities.MenuItemsEntity;
import org.wolt.woltproject.entities.OrderEntity;
import org.wolt.woltproject.entities.UserEntity;
import org.wolt.woltproject.enums.OrderStatusEnum;
import org.wolt.woltproject.exceptions.ActivationException;
import org.wolt.woltproject.exceptions.NotAccessibleMethod;
import org.wolt.woltproject.exceptions.NotFoundException;
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

    private final MailConfig mailConfig;


    public void addOrCreateOrder(Integer userId, Integer item) {
        log.info("ActionLog.OrderService.addOrCreateOrder method is started for id {}", userId);


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
        MenuItemsEntity addedItem = itemRepository.findById(item).orElseThrow(() -> new NotFoundException("Item Not Found"));
        orderEntity.getItems().add(addedItem);
        orderEntity.setTotalAmount(orderEntity.getTotalAmount() + addedItem.getPrice());
        userEntity.getOrderEntity().add(orderEntity);
        repository.save(orderEntity);
        log.info("ActionLog.OrderService.addOrCreateOrder method is finished for id {}", userId);
    }

    public OrderResponseDto getOrderList(Integer userId) {
        log.info("ActionLog.OrderService.getOrderList method is started for id {}", userId);
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User Not Found");
        }
        UserEntity user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User Not Found"));
        OrderEntity order;
        if (repository.findByStatusAndUserId(OrderStatusEnum.PENDING, user).isPresent()) {
            order = (OrderEntity) repository.findByStatusAndUserId(OrderStatusEnum.PENDING, user).orElse(null);
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
        log.info("ActionLog.OrderService.getOrderList method is finished for id {}", userId);

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
    public void deleteItem(Integer userId, Integer itemId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User Not Found");
        }
        log.info("ActionLog.OrderService.deleteItem method is started for id {}", userId);
        UserEntity user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User Not Found"));
        if (repository.findByUserId(userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User Not Found"))).isEmpty()) {
            throw new NotFoundException("There is not order");
        }
        OrderEntity order =  repository.findByStatusAndUserId(OrderStatusEnum.PENDING, user).orElseThrow(() -> new NotFoundException("Order Not Found"));
        List<MenuItemsEntity> list = order.getItems();
        boolean has = false;
        MenuItemsEntity item;
        for (int i = 0; i < list.size(); i++) {
            item = list.get(i);
            if (Objects.equals(item.getMenuItemId(), itemId)) {
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
        log.info("ActionLog.OrderService.deleteItem method is finished for id {}", userId);
    }

    public void clear(Integer userId) {
        log.info("ActionLog.OrderService.clear method is started for id {}", userId);
        UserEntity user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Not Found User"));
        OrderEntity order = repository.findByStatusAndUserId(OrderStatusEnum.PENDING, user).orElseThrow(() -> new NotFoundException("Order Not Found"));
        if (order.getItems().isEmpty()) {
            throw new NotFoundException("Not Found any Order");
        }
        user.setOrderEntity(null);
        userRepository.save(user);
        log.info("ActionLog.OrderService.clear method is finished for id {}", userId);
    }


    public List<OrderResponseDto> showOrdersByRestaurant(Integer restaurantId) {
        log.info("ActionLog.OrderService.showOrdersByRestaurant method is started for id {}", restaurantId);
        List<OrderEntity> orderList = repository.findAllByStatusIs(OrderStatusEnum.ACCEPTED);
        List<OrderEntity> restaurantOrderList = new ArrayList<>();
        for (OrderEntity entity : orderList) {
            List<MenuItemsEntity> items = entity.getItems();
            if (Objects.equals(items.get(0).getMenu().getRestaurant().getRestaurantId(), restaurantId)) {
                restaurantOrderList.add(entity);
            }
        }

        List<OrderResponseDto> orderDto = restaurantOrderList.stream().map(map::toDto).toList();
        log.info("ActionLog.OrderService.showOrdersByRestaurant method is finished for id {}", restaurantId);
        return orderDto;
    }

    public void prepareOrder(HttpServletRequest request, Integer orderId) {
        Integer id = jwtService.getUserId(jwtService.resolveClaims(request));
        UserEntity userEntity = userRepository.findById(id).orElseThrow(() -> new NotFoundException("User Not Found"));
        Integer restaurantId =  userEntity.getRestaurant().getRestaurantId();
        log.info("ActionLog.OrderService.prepareOrder method is started for id {}",restaurantId);
        for (int i = 0; i < showOrdersByRestaurant(restaurantId).size(); i++) {
            if (Objects.equals(showOrdersByRestaurant(restaurantId).get(i).getOrderId(), orderId)) {
                OrderEntity order = repository.findById(orderId).orElseThrow(() -> new NotFoundException("Not Found Order"));
                order.setStatus(OrderStatusEnum.PREPARING);
                repository.save(order);
            }
        }
        log.info("ActionLog.OrderService.prepareOrder method is finished for id {}",restaurantId);

    }

    public void takeOrder(Integer courierId, Integer orderId) {
        log.info("ActionLog.OrderService.takeOrder method is started for id {}", orderId);
        UserEntity userEntity = userRepository.findById(courierId).orElseThrow(() -> new NotFoundException("Courier Not Found"));
        OrderEntity order = repository.findById(orderId).orElseThrow(() -> new NotFoundException("Order Not Found"));
        if (order.getStatus().equals(OrderStatusEnum.PREPARING)) {
            order.setStatus(OrderStatusEnum.IN_COURIER);
            repository.save(order);
        } else {
            throw new NotAccessibleMethod("Not Accessible Method");
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

        log.info("ActionLog.OrderService.takeOrder method is finished for id {}", orderId);
    }

    public boolean deliverOrder(Integer orderId) {
        log.info("ActionLog.OrderService.deliverOrder method is started for id {}", orderId);
        OrderEntity order = repository.findById(orderId).orElseThrow(() -> new NotFoundException("Order Not Found"));
        if (order.getStatus().equals(OrderStatusEnum.IN_COURIER)) {
            order.setStatus(OrderStatusEnum.ARRIVED);
            repository.save(order);
            log.info("ActionLog.OrderService.deliverOrder method is finished for id {}", orderId);
            return true;
        }else{
            throw new NotFoundException("Order is not 'IN_COURIER' status");
        }
    }


}
