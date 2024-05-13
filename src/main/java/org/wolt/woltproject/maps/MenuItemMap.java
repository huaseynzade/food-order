package org.wolt.woltproject.maps;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.wolt.woltproject.entities.MenuItemsEntity;
import org.wolt.woltproject.models.MenuItemDto;

@Mapper(componentModel = "spring")
public interface MenuItemMap {
    MenuItemsEntity toEntity(MenuItemDto dto);

    @Mapping(source = "menu.menuId",target = "menuId")
    MenuItemDto toDto(MenuItemsEntity entity);
}
