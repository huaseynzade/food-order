package org.wolt.woltproject.controllers;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.wolt.woltproject.models.OrderResponseDto;
import org.wolt.woltproject.services.OrderService;

import java.util.List;

@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @Operation(
            summary = "Create or Update Order (Cart)",
            description = "You can create a order. If You have then method will just add to it."
    )
    @PostMapping("/{item}")
    @ResponseStatus(HttpStatus.OK)
    public void addOrCreate(@RequestHeader Integer userId,
                            @PathVariable Integer item) {
        orderService.addOrCreateOrder(userId,item);
    }

    @Operation(
            summary = "Show Order List",
            description = "You can see Order Content with this method.(If status is PENDING)"
    )
    @GetMapping
    public OrderResponseDto getOrderList(@RequestHeader Integer userId) {
        return orderService.getOrderList(userId);
    }

    @Operation(
            summary = "Delete Item from Order",
            description = "You can see delete item from Order"
    )
    @DeleteMapping("/{itemId}")
    public void deleteItem(
            @RequestHeader Integer userId,
            @PathVariable Integer itemId) {
        orderService.deleteItem(userId, itemId);
    }

    @Operation(
            summary = "Clear Order List",
            description = "This method clears order list and makes it empty"
    )
    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void clear(@RequestHeader Integer userId) {
        orderService.clear(userId);
    }



    @Operation(
            summary = "Show Waiting orders to Restaurant",
            description = "Restaurant can see waiting orders. This Method informs the restaurant"
    )
    @GetMapping("/{restaurantId}")
    public List<OrderResponseDto> showOrdersByRestaurant(@PathVariable Integer restaurantId) {
        return orderService.showOrdersByRestaurant(restaurantId);
    }

    @Operation(
            summary = "Prepare Order Method for Restaurants",
            description = "Restaurant can take and start to prepare orders with order's id (If Status is ACCEPTED). You have to login as restaurant owner for run this method."
    )
    @PutMapping("/{restaurantId}/prepare/{orderId}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void prepareOrder(HttpServletRequest request,
                             @PathVariable Integer orderId) {
        orderService.prepareOrder(request,orderId);
    }

    @Operation(
            summary = "Show Order History",
            description = "You can see Order History with this method (Whatever the status is)"
    )
    @GetMapping("/history")
    public List<OrderResponseDto> getPastOrders(HttpServletRequest request) {
        return orderService.getPastOrders(request);
    }

    @Operation(
            summary = "Take Order Method for Couriers",
            description = "Courier Takes Order (If Status Preparing)"
    )
    @PutMapping("/{courierId}/take/{orderId}")
    public void takeOrder(@PathVariable Integer courierId,@PathVariable Integer orderId) {
        orderService.takeOrder(courierId,orderId);
    }


    @Operation(
            summary = "Deliver order Method for couriers",
            description = "Courier Delivers Order (If Status IN_COURIER)"
    )
    @PutMapping("/deliver/{orderId}")
    public boolean deliverOrder(@PathVariable Integer orderId) {
        return orderService.deliverOrder(orderId);
    }


}
