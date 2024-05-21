package org.wolt.woltproject.services;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.wolt.woltproject.entities.UserEntity;
import org.wolt.woltproject.exceptions.NotFoundException;
import org.wolt.woltproject.maps.UserMap;
import org.wolt.woltproject.models.UserRequestDto;
import org.wolt.woltproject.models.UserResponseDto;
import org.wolt.woltproject.repositories.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository repository;
    private final UserMap map;
    private final JwtService jwtService;


    public UserResponseDto showUser(Integer id){
        log.info("ActionLog.UserService.showUser method is started for user id {}", id);
        UserResponseDto userResponseDto = map.toDto(repository.findById(id).orElseThrow(() -> new NotFoundException("User Not Found")));
        log.info("ActionLog.UserService.showUser method is finished for user id {}", id);
        return userResponseDto;
    }
    public List<UserResponseDto> showAllUsers(){
        log.info("ActionLog.UserService.showAllUsers method is started");
        List<UserResponseDto> list = repository.findAll().stream().map(map::toDto).toList();
        log.info("ActionLog.UserService.showAllUsers method is started");
        return list;
    }
    public void updateUser(HttpServletRequest request, UserRequestDto dto){
        Integer id = jwtService.getUserId(jwtService.resolveClaims(request));
        log.info("ActionLog.UserService.updateUser method is started for id {}", id);
        repository.findById(id).orElseThrow(() -> new NotFoundException("User Not Found"));
        dto.setUserId(id);
        repository.save(map.toEntity(dto));
        log.info("ActionLog.UserService.updateUser method is finished for id {}", id);
    }
    public void deleteUser(Integer id){
        log.info("ActionLog.UserService.deleteUser method is started for id {}", id);
        repository.findById(id).orElseThrow(() -> new NotFoundException("User Not Found"));
        repository.deleteById(id);
        log.info("ActionLog.UserService.deleteUser method is finished for id {}", id);

    }

}
