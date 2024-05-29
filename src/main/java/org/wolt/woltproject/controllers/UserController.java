package org.wolt.woltproject.controllers;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.wolt.woltproject.models.UserRequestDto;
import org.wolt.woltproject.models.UserResponseDto;
import org.wolt.woltproject.services.UserService;

import java.util.List;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService service;


    @Operation(
            summary = "Show User's Profile"
    )
    @GetMapping("/profile")
    public UserResponseDto showProfile(HttpServletRequest request) {
        return service.showProfile(request);
    }

    @Operation(
            summary = "Show All Users"
    )
    @GetMapping
    public List<UserResponseDto> showAllUsers() {
        return service.showAllUsers();
    }


    @Operation(
            summary = "Show User By Id"
    )
    @GetMapping("/{id}")
    public UserResponseDto showUser(@PathVariable Integer id) {
        return service.showUser(id);
    }


    @Operation(
            summary = "Update User"
    )
    @PutMapping
    public void updateUser(HttpServletRequest request,@RequestBody @Valid UserRequestDto dto) {
        service.updateUser(request, dto);
    }

    @Operation(
            summary = "Delete User"
    )
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable Integer id) {
        service.deleteUser(id);
    }


}
