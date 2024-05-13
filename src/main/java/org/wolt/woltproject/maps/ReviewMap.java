package org.wolt.woltproject.maps;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.wolt.woltproject.entities.ReviewsEntity;
import org.wolt.woltproject.models.ReviewResponseDto;
import org.wolt.woltproject.models.ReviewsDto;

@Mapper(componentModel = "spring")
public interface ReviewMap {
    @Mapping(source = "restaurant.restaurantId",target ="restaurantId")
    @Mapping(source = "user.username", target = "username")
    ReviewResponseDto toDto(ReviewsEntity entity);


    @Mapping(source = "restaurantId",target ="restaurant.restaurantId")
    ReviewsEntity toEntity(ReviewsDto dto);

}
