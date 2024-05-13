package org.wolt.woltproject.maps;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.wolt.woltproject.entities.CardEntity;
import org.wolt.woltproject.models.CardRequestDto;
import org.wolt.woltproject.models.CardResponseDto;

@Mapper(componentModel = "spring")
public interface CardMap {

    CardEntity toEntity(CardRequestDto dto);

    CardResponseDto toDto(CardEntity entity);
}
