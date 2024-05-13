package org.wolt.woltproject.controllers;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.wolt.woltproject.models.LoginRequest;
import org.wolt.woltproject.models.LoginResponse;
import org.wolt.woltproject.models.UserRequestDto;
import org.wolt.woltproject.services.AuthService;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService service;

    @PostMapping("/register")
    public void register(@RequestBody UserRequestDto userRequestDto) {
        service.register(userRequestDto);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(LoginRequest request) {
        return service.login(request);
    }

    @PutMapping("/activation/sendcode")
    public void sendActivationMail(HttpServletRequest request){
        service.sendActivationMail(request);
    }

    @PostMapping("/activation/inputcode/{activationCode}")
    public Boolean inputCode(@PathVariable Integer activationCode, HttpServletRequest request) {
        return service.inputCode(activationCode, request);
    }






}
