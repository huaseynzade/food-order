package org.wolt.woltproject.controllers;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.wolt.woltproject.models.OrderResponseDto;
import org.wolt.woltproject.services.OrderService;

import java.util.List;

@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;


    @PostMapping("/{item}")
    public void addOrCreate(@RequestHeader Integer userId,
                            @PathVariable Integer item) {
        orderService.addOrCreateOrder(userId,item);
    }

    @GetMapping
    public OrderResponseDto getOrderList(@RequestHeader Integer userId) {
        return orderService.getOrderList(userId);
    }

    @DeleteMapping("/{itemId}")
    public void deleteItem(
            @RequestHeader Integer userId,
            @PathVariable Integer itemId) {
        orderService.deleteItem(userId, itemId);
    }

    @DeleteMapping("/clear")
    public void clear(@RequestHeader Integer userId) {
        orderService.clear(userId);
    }


    @GetMapping("/restaurant/{restaurantId}/show-orders")
    public List<OrderResponseDto> showOrdersByRestaurant(@PathVariable Integer restaurantId) {
        return orderService.showOrdersByRestaurant(restaurantId);
    }

    @PutMapping("/restaurant/{restaurantId}/take-order/{orderId}")
    public void prepareOrder(@PathVariable Integer restaurantId,
                             @PathVariable Integer orderId) {
        orderService.prepareOrder(restaurantId,orderId);
    }

    @GetMapping("/get-all")
    public List<OrderResponseDto> getPastOrders(HttpServletRequest request) {
        return orderService.getPastOrders(request);
    }

    @PutMapping("/courier/{courierId}/order/{orderId}")
    public void takeOrder(@PathVariable Integer courierId,@PathVariable Integer orderId) {
        orderService.takeOrder(courierId,orderId);
    }
}
