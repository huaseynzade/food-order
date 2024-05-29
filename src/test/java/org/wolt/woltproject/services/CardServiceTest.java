package org.wolt.woltproject.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.wolt.woltproject.entities.CardEntity;
import org.wolt.woltproject.entities.UserEntity;
import org.wolt.woltproject.exceptions.NotFoundException;
import org.wolt.woltproject.maps.CardMap;
import org.wolt.woltproject.models.CardRequestDto;
import org.wolt.woltproject.models.CardResponseDto;
import org.wolt.woltproject.repositories.CardRepository;
import org.wolt.woltproject.repositories.UserRepository;
import org.wolt.woltproject.services.CardService;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class CardServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private CardRepository repository;

    @Mock
    private CardMap cardMap;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private CardService cardService;

    @Test
    public void testCreateNewCardWithValidUser() {

        CardRequestDto cardRequestDto = new CardRequestDto();
        cardRequestDto.setUserId(1);
        CardEntity cardEntity = new CardEntity();
        cardEntity.setCardId(1);
        UserEntity userEntity = new UserEntity();
        userEntity.setUserId(1);
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(jwtService.getUserId(any())).thenReturn(1);
        when(jwtService.resolveClaims(request)).thenReturn(mock(Claims.class));
        when(userRepository.findById(1)).thenReturn(Optional.empty());


        when(userRepository.findById(1)).thenReturn(Optional.of(userEntity));

        when(cardMap.toEntity(cardRequestDto)).thenReturn(cardEntity);

        cardService.createNewCard(cardRequestDto, request);

        verify(userRepository, times(1)).findById(1);
        verify(cardMap, times(1)).toEntity(cardRequestDto);
        verify(repository, times(1)).save(cardEntity);
    }

    @Test
    public void testCreateNewCardWithInvalidUser() {
        CardRequestDto cardRequestDto = new CardRequestDto();
        cardRequestDto.setUserId(1);
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(jwtService.getUserId(any())).thenReturn(1);
        when(jwtService.resolveClaims(request)).thenReturn(mock(Claims.class));
        when(userRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> {
            cardService.createNewCard(cardRequestDto, request);
        });

        verify(userRepository, times(1)).findById(1);
        verifyNoInteractions(cardMap); // cardMap ile etkileşim olmamalıdır
        verifyNoInteractions(repository); // repository ile etkileşim olmamalıdır
    }
    @Test
    public void testShowCards() {
        CardEntity card1 = new CardEntity();
        card1.setCardId(1);
        CardEntity card2 = new CardEntity();
        card2.setCardId(2);

        CardResponseDto cardResponseDto1 = new CardResponseDto();
        cardResponseDto1.setCardId(1);
        CardResponseDto cardResponseDto2 = new CardResponseDto();
        cardResponseDto2.setCardId(2);

        when(repository.findAll()).thenReturn(Arrays.asList(card1, card2));
        when(cardMap.toDto(card1)).thenReturn(cardResponseDto1);
        when(cardMap.toDto(card2)).thenReturn(cardResponseDto2);

        List<CardResponseDto> result = cardService.showCards();

        assertEquals(2, result.size());
        assertEquals(1, result.get(0).getCardId());
        assertEquals(2, result.get(1).getCardId());

        verify(repository, times(1)).findAll();
        verify(cardMap, times(1)).toDto(card1);
        verify(cardMap, times(1)).toDto(card2);
    }

//    @Test
//    public void testShowCardsOfUser() {
//        Integer userId = 1;
//
//        UserEntity userEntity = new UserEntity();
//        userEntity.setUserId(userId);
//        CardEntity card1 = new CardEntity();
//        card1.setCardId(1);
//        CardEntity card2 = new CardEntity();
//        card2.setCardId(2);
//        userEntity.setCardEntity(Arrays.asList(card1, card2));
//
//        CardResponseDto cardResponseDto1 = new CardResponseDto();
//        cardResponseDto1.setCardId(1);
//        CardResponseDto cardResponseDto2 = new CardResponseDto();
//        cardResponseDto2.setCardId(2);
//
//        when(userRepository.findById(userId)).thenReturn(java.util.Optional.of(userEntity));
//        when(cardMap.toDto(card1)).thenReturn(cardResponseDto1);
//        when(cardMap.toDto(card2)).thenReturn(cardResponseDto2);
//
//        List<CardResponseDto> result = cardService.showCardsOfUser(userId);
//
//        assertEquals(2, result.size());
//        assertEquals(1, result.get(0).getCardId());
//        assertEquals(2, result.get(1).getCardId());
//
//        verify(userRepository, times(1)).findById(userId);
//        verify(cardMap, times(1)).toDto(card1);
//        verify(cardMap, times(1)).toDto(card2);
//    }

    @Test
    public void testDeleteCard() {
        Integer cardId = 1;

        CardEntity cardEntity = new CardEntity();
        cardEntity.setCardId(cardId);

        when(repository.findById(cardId)).thenReturn(java.util.Optional.of(cardEntity));

        cardService.deleteCard(cardId);

        verify(repository, times(1)).deleteById(cardId);
    }
}
