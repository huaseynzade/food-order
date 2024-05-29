package org.wolt.woltproject.services;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.wolt.woltproject.entities.UserEntity;
import org.wolt.woltproject.maps.UserMap;
import org.wolt.woltproject.models.UserRequestDto;
import org.wolt.woltproject.models.UserResponseDto;
import org.wolt.woltproject.repositories.UserRepository;

import java.util.Collections;
import java.util.Optional;

import static org.mockito.Mockito.*;

public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMap userMap;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testShowUser() {
        // Given
        int userId = 1;
        UserEntity userEntity = new UserEntity();
        when(userRepository.findById(userId)).thenReturn(Optional.of(userEntity));
        when(userMap.toDto(userEntity)).thenReturn(new UserResponseDto());

        // When
        userService.showUser(userId);

        // Then
        verify(userRepository).findById(userId);
        verify(userMap).toDto(userEntity);
    }

    @Test
    public void testShowAllUsers() {
        // Given
        when(userRepository.findAll()).thenReturn(Collections.emptyList());

        // When
        userService.showAllUsers();

        // Then
        verify(userRepository).findAll();
        verify(userMap, never()).toDto(any());
    }

    @Test
    public void testUpdateUser() {
        // Given
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(jwtService.getUserId(any())).thenReturn(1);
        when(jwtService.resolveClaims(request)).thenReturn(mock(Claims.class));
        when(userMap.toEntity(any())).thenReturn(new UserEntity());

        // When
        userService.updateUser(request, new UserRequestDto());

        // Then
        verify(jwtService).getUserId(any());
        verify(jwtService).resolveClaims(request);
        verify(userRepository).findById(1);
        verify(userMap).toEntity(any());
        verify(userRepository).save(any());
    }
}
