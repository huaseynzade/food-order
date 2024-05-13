package org.wolt.woltproject.maps;

import org.mapstruct.Mapper;
import org.wolt.woltproject.entities.UserEntity;
import org.wolt.woltproject.models.UserRequestDto;
import org.wolt.woltproject.models.UserResponseDto;

@Mapper(componentModel = "spring")
public interface UserMap {

    UserEntity toEntity(UserRequestDto dto);
    UserResponseDto toDto(UserEntity entity);

}
