package org.wolt.woltproject.controllers;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.wolt.woltproject.entities.OrderEntity;
import org.wolt.woltproject.entities.RestaurantEntity;
import org.wolt.woltproject.models.OrderCourierDto;
import org.wolt.woltproject.models.OrderResponseDto;
import org.wolt.woltproject.services.OrderService;

import java.io.IOException;
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
    @ResponseStatus(HttpStatus.CREATED) //jwt
    public void addOrCreate(HttpServletRequest request,
                            @PathVariable Integer item) {
        orderService.addOrCreateOrder(request,item);
    }

    @Operation(
            summary = "Show Order List",
            description = "You can see Order Content with this method.(If status is PENDING)"
    )
    @GetMapping
    public OrderResponseDto getOrder(HttpServletRequest request) {
        return orderService.getOrder(request);
    }

    @Operation(
            summary = "Delete Item from Order",
            description = "You can see delete item from Order"
    )
    @DeleteMapping("/{itemId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteItem(
            HttpServletRequest request,
            @PathVariable Integer itemId) {
        orderService.deleteItem(request, itemId);
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
            summary = "Prepare Order Method for Restaurants",
            description = "Restaurant can take and start to prepare orders with order's id (If Status is ACCEPTED). You have to login as restaurant owner for run this method."
    )
    @PutMapping("/prepare/{orderId}")
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
    @PutMapping("/take/{orderId}")
    public String takeOrder(HttpServletRequest request,@PathVariable Integer orderId) throws IOException {
        return orderService.takeOrder(request,orderId);
    }

    @GetMapping("/courier/all")
    public List<OrderCourierDto> showOrdersCourier() {
        return orderService.showOrdersCourier();
    }

    @GetMapping("/restaurant/all")
    public List<OrderResponseDto> showOrdersRestaurant(HttpServletRequest request) {
        return orderService.showOrdersRestaurant(request);
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
