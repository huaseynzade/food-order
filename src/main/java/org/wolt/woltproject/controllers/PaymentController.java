package org.wolt.woltproject.controllers;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.wolt.woltproject.models.PaymentRequestDto;
import org.wolt.woltproject.models.PaymentResponseDto;
import org.wolt.woltproject.services.PaymentService;

import java.util.List;

@RestController
@RequestMapping("/payment")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService service;


    @Operation(
            summary = "Pay for order",
            description = "Method provides you to pay for any order"
    )
    @PostMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void create(@RequestBody @Valid PaymentRequestDto dto) {
        service.create(dto);
    }

    @Operation(
            summary = "Get Payment History",
            description = "See Payment History"
    )
    @GetMapping("/{userId}/history")
    public List<PaymentResponseDto> getHistory(@PathVariable Integer userId) {
        return service.getHistory(userId);
    }

    @Operation(
            summary = "Get Payment with its Id",
            description = "Can see Payment with its id"
    )
    @GetMapping("/{id}")
    public PaymentResponseDto getById(@PathVariable Integer id) {
        return service.getById(id);
    }

    @Operation(
            summary = "Cancel payment",
            description = "If Order isn't preparing (Status is just ACCEPTED) then can cancel payment"
    )
    @PutMapping("/cancel/{id}")
    public void cancelOrder(@PathVariable Integer id) {
        service.cancelOrder(id);
    }
}
