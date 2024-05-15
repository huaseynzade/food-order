package org.wolt.woltproject.controllers;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.wolt.woltproject.models.CardRequestDto;
import org.wolt.woltproject.models.CardResponseDto;
import org.wolt.woltproject.models.PostValidation;
import org.wolt.woltproject.services.CardService;

import java.util.List;

@RestController
@RequestMapping("/card")
@RequiredArgsConstructor
public class CardController {
    private final CardService service;

    @Operation(
            summary = "Show All Cards in System (secure)",
            description = ""
    )
    @GetMapping
    public List<CardResponseDto> showCards(){
        return service.showCards();
    }

    @Operation(
            summary = "Create a Card",
            description = "You can create a new card with this method. After create it you can add card to a user"
    )
    @PostMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void createNewCard(@RequestBody @Validated(value = PostValidation.class) CardRequestDto cardRequestDto){
        service.createNewCard(cardRequestDto);
    }

    @Operation(
            summary = "Show User's Card",
            description = "This method shows user's all cards."
    )
    @GetMapping("/{userId}")
    public List<CardResponseDto> showCardsOfUser(@PathVariable Integer userId){
        return service.showCardsOfUser(userId);
    }


    @Operation(
            summary = "Delete User's Card"
    )
    @DeleteMapping("/{deleteId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCard(@PathVariable Integer deleteId){
        service.deleteCard(deleteId);
    }


}
