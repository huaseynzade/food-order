package org.wolt.woltproject.services;

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


    public UserResponseDto showUser(Integer id){
        log.info("Show User Method is working");
        if (!repository.existsById(id)){
            throw new NotFoundException("Not Found");
        }
        return map.toDto(repository.findById(id).orElseThrow(() -> new NotFoundException("User Not Found")));
    }
    public List<UserResponseDto> showAllUsers(){
        log.info("Show All Users Method is working");
        return repository.findAll().stream().map(element -> map.toDto((UserEntity) element)).toList();
    }
    public void updateUser(Integer id, UserRequestDto dto){
        log.info("{} id is changing", id);
        if (!repository.existsById(id)){
            throw new NotFoundException("User Not Found");
        }
        dto.setUserId(id);
        repository.save(map.toEntity(dto));
        log.info("{} id has changed",id);
    }
    public void deleteUser(Integer id){
        repository.findById(id).orElseThrow(() -> new NotFoundException("User Not Found"));
        repository.deleteById(id);
    }




}
