package org.wolt.woltproject.controllers;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.wolt.woltproject.models.LoginRequest;
import org.wolt.woltproject.models.LoginResponse;
import org.wolt.woltproject.models.ResetPasswordDto;
import org.wolt.woltproject.models.UserRequestDto;
import org.wolt.woltproject.services.AuthService;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService service;


    @Operation(
            summary = "Register",
            description = "This method allows Register for everyone to Food Order System"
    )
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public void register(@RequestBody @Valid UserRequestDto userRequestDto) {
        service.register(userRequestDto);
    }

    @Operation(
            summary = "Login",
            description = "This method allows to log in their accounts for everyone"
    )
    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
        public ResponseEntity<?> login(@RequestBody @Valid LoginRequest request) {
        return service.login(request);
    }

    @Operation(
            summary = "Send Code to email for activation",
            description = "This method sends mail for activate mail"
    )
    @PutMapping("/send")
    @ResponseStatus(HttpStatus.OK)
    public void sendActivationMail(HttpServletRequest request){
        service.sendActivationMail(request);
    }

    @Operation(
            summary = "Send your account's email if you forgot the password",
            description = "This method sends mail for reset your password"
    )
    @PostMapping("/forgot/{email}")
    public void forgotPassword(@PathVariable String email, HttpSession session) {
        service.forgotPassword(email, session);
    }

    @PutMapping("/reset/{key}")
    public void changePassword(@RequestBody ResetPasswordDto resetPasswordDto, @PathVariable Integer key, HttpServletRequest request) {
        service.changePassword(resetPasswordDto, key, request);
    }
    @Operation(
            summary = "To Input your activation code and activate your profile",
            description = "This method provides you to input your activation code which is sent to your mail and activate your account. After Activate your account you can order food and do some other actions in system "
    )
    @PostMapping("/activation/{activationCode}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void inputCode(@PathVariable Integer activationCode, HttpServletRequest request) {
         service.inputCode(activationCode, request);
    }
}
