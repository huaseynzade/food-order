package org.wolt.woltproject.controllers;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.wolt.woltproject.enums.RoleEnum;
import org.wolt.woltproject.models.UserRequestDto;
import org.wolt.woltproject.models.UserResponseDto;
import org.wolt.woltproject.services.UserService;

import java.util.List;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService service;

    @GetMapping
    public List<UserResponseDto> showAllUsers() {
        return service.showAllUsers();
    }

    @GetMapping("/{id}")
    public UserResponseDto showUser(@PathVariable Integer id) {
        return service.showUser(id);
    }


    @PutMapping()
    public void updateUser(@RequestHeader Integer id,@RequestBody UserRequestDto dto) {
        service.updateUser(id, dto);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Integer id) {
        service.deleteUser(id);
    }

    public void showUsersByRole(RoleEnum role){

    }
}
