package org.wolt.woltproject.maps;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.wolt.woltproject.entities.OrderEntity;
import org.wolt.woltproject.models.OrderCourierDto;
import org.wolt.woltproject.models.OrderResponseDto;

@Mapper(componentModel = "spring")
public interface OrderMap{

    @Mapping(source ="userId.username", target = "userName")
    @Mapping(source = "items",target = "items")
    OrderResponseDto toDto(OrderEntity entity);

    @Mapping(source = "userName",target = "userId.username")
    @Mapping(source = "items",target = "items")
    OrderEntity toEntity(OrderResponseDto dto);


    @Mapping(source ="userId.username", target = "userName")
    OrderCourierDto toCourierDto(OrderEntity entity);
}
