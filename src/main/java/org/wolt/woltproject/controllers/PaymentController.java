package org.wolt.woltproject.controllers;

import lombok.RequiredArgsConstructor;
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


    @PostMapping
    public void create(@RequestBody PaymentRequestDto dto) {
        service.create(dto);
    }

    @GetMapping("/user/{userId}/history")
    public List getHistory(@PathVariable Integer userId) {
        return service.getHistory(userId);
    }

    @GetMapping("/{id}")
    public PaymentResponseDto getById(@PathVariable Integer id) {
        return service.getById(id);
    }

    @PutMapping("/cancel/payment/{id}")
    public void cancelOrder(@PathVariable Integer id) {
        service.cancelOrder(id);
    }
}
