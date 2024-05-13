package org.wolt.woltproject.maps;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.wolt.woltproject.entities.MenuEntity;
import org.wolt.woltproject.entities.RestaurantEntity;
import org.wolt.woltproject.models.MenuDto;
import org.wolt.woltproject.models.RestaurantDto;

@Mapper(componentModel = "spring")
public interface MenuMap {
//    @Mapping(source = "restaurantDto",target = "restaurant")
    MenuEntity toEntity(MenuDto dto);
//    @Mapping(source = "restaurant",target = "restaurantDto")

    MenuDto toDto(MenuEntity entity);

}
