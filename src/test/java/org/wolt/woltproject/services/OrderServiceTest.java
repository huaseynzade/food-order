package org.wolt.woltproject.services;

import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.annotation.Before;

import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.wolt.woltproject.entities.OrderEntity;
import org.wolt.woltproject.entities.UserEntity;
import org.wolt.woltproject.enums.OrderStatusEnum;
import org.wolt.woltproject.exceptions.ActivationException;
import org.wolt.woltproject.exceptions.NotFoundException;
import org.wolt.woltproject.models.OrderResponseDto;
import org.wolt.woltproject.repositories.OrderRepository;
import org.wolt.woltproject.repositories.UserRepository;
import org.wolt.woltproject.services.JwtService;
import org.wolt.woltproject.services.OrderService;
//import jakarta.mail.*;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private OrderService orderService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }
//    @Test
//    public void testGetOrderListWithValidUserAndOrder() {
//
//        UserEntity userEntity = new UserEntity();
//        userEntity.setUserId(1);
//        OrderEntity orderEntity = new OrderEntity();
//        orderEntity.setOrderId(1);
//
//        when(userRepository.findById(1)).thenReturn(Optional.of(userEntity));
//
//        when(orderRepository.findByStatusAndUserId(OrderStatusEnum.PENDING, userEntity)).thenReturn(Optional.of(orderEntity));
//
//        OrderResponseDto dto = orderService.getOrderList(1);
//
//
//        assertNotNull(dto);
//        assertEquals(1, dto.getOrderId());
//
//        verify(userRepository, times(1)).findById(1);
//        verify(orderRepository, times(1)).findByStatusAndUserId(OrderStatusEnum.PENDING, userEntity);
//    }




}